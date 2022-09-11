/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.block.transportstorage.transporter;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.FilteredTransporterType;
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.proxy.client.render.TransporterTESR;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.Sets;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.TileUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TransporterItemType extends FilteredTransporterType<ItemStack, IItemHandler> {

    public static final int QUEUE_SIZE = 6;

    private HashMap<Direction, List<ItemStack>> queue;
    private int extractSlot;

    public TransporterItemType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.queue = new HashMap<>();
        this.extractSlot = 0;
        for (Direction value : Direction.values()) {
            while (this.queue.computeIfAbsent(value, direction -> new ArrayList<>()).size() < QUEUE_SIZE) {
                this.queue.get(value).add(0, ItemStack.EMPTY);
            }
            if (this.queue.size() > QUEUE_SIZE) {
                this.queue.get(value).remove(this.queue.get(value).size() - 1);
            }
        }
    }

    @Override
    public RegulatorFilter<ItemStack, IItemHandler> createFilter() {
        return new RegulatorFilter<ItemStack, IItemHandler>(20, 20, 5, 3, 16, 64, 1024 * 8, "") {
            @Override
            public int matches(ItemStack stack, IItemHandler itemHandler, boolean isRegulated) {
                if (isEmpty()) return stack.getCount();
                int amount = 0;
                if (isRegulated) {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (itemHandler.getStackInSlot(i).sameItem(stack)) {
                            amount += itemHandler.getStackInSlot(i).getCount();
                        }
                    }
                }

                for (IFilter.GhostSlot slot : this.getFilter()) {
                    if (stack.sameItem(slot.getStack())) {
                        int maxAmount = isRegulated ? slot.getAmount() : Integer.MAX_VALUE;
                        int returnAmount = Math.min(stack.getCount(), maxAmount - amount);
                        if (returnAmount > 0) return returnAmount;
                    }
                }
                return 0;
            }
        };
    }

    @Override
    public void update() {
        super.update();
        float speed = getSpeed();
        if (!getLevel().isClientSide && getLevel().getGameTime() % (Math.max(1, 4 - speed)) == 0) {
            IBlockContainer container = getContainer();
            if (getAction() == TransporterTypeFactory.TransporterAction.EXTRACT && container instanceof TransporterTile) {
                for (Direction direction : ((TransporterTile) container).getTransporterTypeMap().keySet()) {
                    TransporterType transporterType = ((TransporterTile) container).getTransporterTypeMap().get(direction);
                    if (transporterType instanceof TransporterItemType && transporterType.getAction() == TransporterTypeFactory.TransporterAction.INSERT) {
                        TileUtil.getTileEntity(getLevel(), getPos().relative(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                            TileUtil.getTileEntity(getLevel(), getPos().relative(direction)).ifPresent(otherTile -> otherTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(destination -> {
                                if (extractSlot >= origin.getSlots() || origin.getStackInSlot(extractSlot).isEmpty()
                                        || !filter(this.getFilter(), this.isWhitelist(), origin.getStackInSlot(extractSlot), origin, false)
                                        || !filter(((TransporterItemType) transporterType).getFilter(), ((TransporterItemType) transporterType).isWhitelist(), origin.getStackInSlot(extractSlot), destination, ((TransporterItemType) transporterType).isRegulated()))
                                    findSlot(origin, ((TransporterItemType) transporterType).getFilter(), ((TransporterItemType) transporterType).isWhitelist(), destination, ((TransporterItemType) transporterType).isRegulated());
                                if (extractSlot >= origin.getSlots()) return;
                                if (!origin.getStackInSlot(extractSlot).isEmpty()) {
                                    int amount = (int) (1 * getEfficiency());
                                    ItemStack extracted = origin.extractItem(extractSlot, amount, true);
                                    int simulatedAmount = ((TransporterItemType) transporterType).getFilter().matches(extracted, destination, ((TransporterItemType) transporterType).isRegulated());
                                    if (!extracted.isEmpty() && filter(this.getFilter(), this.isWhitelist(), extracted, origin, false) && filter(((TransporterItemType) transporterType).getFilter(), ((TransporterItemType) transporterType).isWhitelist(), origin.getStackInSlot(extractSlot), destination, ((TransporterItemType) transporterType).isRegulated()) && simulatedAmount > 0) {
                                        ItemStack returned = ItemHandlerHelper.insertItem(destination, extracted, true);
                                        if (returned.isEmpty() || amount - returned.getCount() > 0) {
                                            extracted = origin.extractItem(extractSlot, returned.isEmpty() ? simulatedAmount : simulatedAmount - returned.getCount(), false);
                                            ItemHandlerHelper.insertItem(destination, extracted, false);
                                            ((TransporterItemType) transporterType).addTransferedStack(getSide(), extracted);
                                        } else {
                                            this.extractSlot++;
                                            if (this.extractSlot >= origin.getSlots()) this.extractSlot = 0;
                                        }
                                    }
                                }
                                container.requestSync();
                            }));
                        }));
                    }
                }
            }
        }
    }

    private boolean filter(RegulatorFilter<ItemStack, IItemHandler> filter, boolean whitelist, ItemStack stack, IItemHandler handler, boolean isRegulated) {
        int accepts = filter.matches(stack, handler, isRegulated);
        if (whitelist && filter.isEmpty()) {
            return false;
        }
        return filter.isEmpty() != (whitelist == (accepts > 0));
    }

    @Override
    public void updateClient() {
        super.updateClient();
        for (Direction value : Direction.values()) {
            while (this.queue.computeIfAbsent(value, direction -> new ArrayList<>()).size() < QUEUE_SIZE) {
                this.queue.get(value).add(0, ItemStack.EMPTY);
            }
            this.queue.get(value).add(0, ItemStack.EMPTY);
            while (this.queue.get(value).size() > QUEUE_SIZE) {
                this.queue.get(value).remove(this.queue.get(value).size() - 1);
            }
        }
    }

    private void findSlot(IItemHandler itemHandler, RegulatorFilter<ItemStack, IItemHandler> otherFilter, boolean otherWhitelist, IItemHandler otherItemHandler, boolean otherRegulated) {
        for (int i = this.extractSlot; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty() && filter(this.getFilter(), this.isWhitelist(), itemHandler.getStackInSlot(i), itemHandler, false) && filter(otherFilter, otherWhitelist, itemHandler.getStackInSlot(i), otherItemHandler, otherRegulated)) {
                this.extractSlot = i;
                return;
            }
        }
        this.extractSlot = 0;
    }

    public void addTransferedStack(Direction direction, ItemStack stack) {
        syncRender(direction, stack.serializeNBT());
    }

    @Override
    public void handleRenderSync(Direction origin, CompoundTag compoundNBT) {
        this.queue.computeIfAbsent(origin, direction -> new ArrayList<>()).add(0, ItemStack.of(compoundNBT));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer, float frame) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer, frame);
        if (step < queue.computeIfAbsent(direction, v -> new ArrayList<>()).size()) {
            float scale = 0.10f;
            stack.scale(scale, scale, scale);
            ItemStack itemStack = queue.get(direction).get(step);
            if (!itemStack.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.NONE, 0xF000F0, combinedOverlayIn, stack, buffer, 0);
            } else {
                stack.pushPose();
                stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                stack.mulPose(Vector3f.ZP.rotationDegrees(90f));
                stack.mulPose(Vector3f.XP.rotationDegrees(90f));
                VertexConsumer buffer1 = buffer.getBuffer(TransporterTESR.TYPE);

                float pX1 = 1;
                float u = 1;
                float pX2 = 0;
                float u2 = 0;
                Color CLOSE = Color.CYAN;
                Color FAR = new Color(0x6800FF);
                double ratio = (step + 2.5) / (double) QUEUE_SIZE;
                float xOffset = -0.75f;
                float yOffset = -0f;
                float zOffset = -0.75f;
                int alpha = 1;
                stack.scale(0.25f, 0.25f, 0.25f);
                float red = (int) Math.abs((ratio * FAR.getRed()) + ((1 - ratio) * CLOSE.getRed())) / 256F;
                float green = (int) Math.abs((ratio * FAR.getGreen()) + ((1 - ratio) * CLOSE.getGreen())) / 256F;
                float blue = (int) Math.abs((ratio * FAR.getBlue()) + ((1 - ratio) * CLOSE.getBlue())) / 256F;
                Matrix4f matrix = stack.last().pose();
                buffer1.vertex(matrix, pX2 + xOffset, yOffset, 0 + zOffset).color(red, green, blue, alpha).uv(u2, 0).endVertex();
                buffer1.vertex(matrix, pX1 + xOffset + 0.5f, yOffset, 0 + zOffset).color(red, green, blue, alpha).uv(u, 0).endVertex();
                buffer1.vertex(matrix, pX1 + xOffset + 0.5f, yOffset, 1.5f + zOffset).color(red, green, blue, alpha).uv(u, 1).endVertex();
                buffer1.vertex(matrix, pX2 + xOffset, yOffset, 1.5f + zOffset).color(red, green, blue, alpha).uv(u2, 1).endVertex();
                stack.popPose();
            }
        }
    }

    public static class Factory extends TransporterTypeFactory {

        public Factory() {
            super("item");
        }

        @Override
        public TransporterType create(IBlockContainer container, Direction face, TransporterAction action) {
            return new TransporterItemType(container, this, face, action);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, TransporterAction action) {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/item_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getSerializedName().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(new ResourceLocation("industrialforegoing:blocks/transporters/item"), new ResourceLocation("industrialforegoing:blocks/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(Level world, BlockPos pos, Direction face) {
            return TileUtil.getTileEntity(world, pos).map(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).isPresent()).orElse(false);
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/item_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getSerializedName().toLowerCase());
        }

        @Override
        public void registerRecipe(Consumer<FinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem(), 2)
                    .pattern("IPI").pattern("GMG").pattern("ICI")
                    .define('I', Tags.Items.DUSTS_REDSTONE)
                    .define('P', Items.ENDER_PEARL)
                    .define('G', Tags.Items.INGOTS_GOLD)
                    .define('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .define('C', Items.PISTON)
                    .save(consumer);
        }
    }
}
