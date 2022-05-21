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
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.Sets;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.TileUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

import com.buuz135.industrial.api.transporter.TransporterTypeFactory.TransporterAction;

public class TransporterWorldType extends FilteredTransporterType<ItemStack, IItemHandler> {

    private int extractSlot;

    public TransporterWorldType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.extractSlot = 0;
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
        if (!getWorld().isClientSide && getWorld().getGameTime() % (Math.max(1, 4 - speed)) == 0) {
            IBlockContainer container = getContainer();
            if (getAction() == TransporterTypeFactory.TransporterAction.EXTRACT && container instanceof TransporterTile) {
                TileUtil.getTileEntity(getWorld(), getPos().relative(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                    if (origin.getStackInSlot(extractSlot).isEmpty() || !filter(this.getFilter(), this.isWhitelist(), origin.getStackInSlot(extractSlot), origin, false))
                        findSlot(origin);
                    if (!origin.getStackInSlot(extractSlot).isEmpty() && filter(this.getFilter(), this.isWhitelist(), origin.getStackInSlot(extractSlot), origin, false)) {
                        int amount = (int) (1 * getEfficiency());
                        ItemStack extracted = origin.extractItem(extractSlot, amount, false);
                        if (extracted.isEmpty()) return;
                        ItemEntity item = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.2, getPos().getZ() + 0.5, extracted);
                        item.setDeltaMovement(0, 0, 0);
                        item.setPickUpDelay(4);
                        item.setItem(extracted);
                        getWorld().addFreshEntity(item);
                    }
                }));
            }
            if (getAction() == TransporterTypeFactory.TransporterAction.INSERT && container instanceof TransporterTile) {
                TileUtil.getTileEntity(getWorld(), getPos().relative(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                    for (ItemEntity item : this.getWorld().getEntitiesOfClass(ItemEntity.class, new AABB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1))) {
                        if (item.isAlive()) {
                            ItemStack stack = item.getItem().copy();
                            int amount = Math.min(stack.getCount(), (int) (1 * getEfficiency()));
                            stack.setCount(amount);
                            amount = this.getFilter().matches(stack, origin, this.isRegulated());
                            if (amount > 0) {
                                stack.setCount(amount);
                                if (!stack.isEmpty() && filter(this.getFilter(), this.isWhitelist(), stack, origin, this.isRegulated())) {
                                    for (int i = 0; i < origin.getSlots(); i++) {
                                        stack = origin.insertItem(i, stack, false);
                                        ItemStack originStack = item.getItem().copy();
                                        originStack.shrink(amount - stack.getCount());
                                        if (originStack.isEmpty()) {
                                            item.setItem(ItemStack.EMPTY);
                                            item.remove(Entity.RemovalReason.KILLED);
                                            return;
                                        } else {
                                            item.setItem(originStack);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }));
            }
        }
    }

    private void findSlot(IItemHandler itemHandler) {
        for (int i = this.extractSlot; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty() && filter(this.getFilter(), this.isWhitelist(), itemHandler.getStackInSlot(i), itemHandler, false)) {
                this.extractSlot = i;
                return;
            }
        }
        this.extractSlot = 0;
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
    }

    @Override
    public void handleRenderSync(Direction origin, CompoundTag compoundNBT) {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer, float frame) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer, frame);

    }

    public static class Factory extends TransporterTypeFactory {

        public Factory() {
            setRegistryName("world");
        }

        @Override
        public TransporterType create(IBlockContainer container, Direction face, TransporterAction action) {
            return new TransporterWorldType(container, this, face, action);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, TransporterAction action) {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/world_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getSerializedName().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(new ResourceLocation("industrialforegoing:blocks/transporters/world"), new ResourceLocation("industrialforegoing:blocks/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(Level world, BlockPos pos, Direction face) {
            return TileUtil.getTileEntity(world, pos).map(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).isPresent()).orElse(false);
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/world_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getSerializedName().toLowerCase());
        }

        @Override
        public void registerRecipe(Consumer<FinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem(), 2)
                    .pattern("IPI").pattern("GMG").pattern("ICI")
                    .define('I', Tags.Items.DUSTS_REDSTONE)
                    .define('P', Items.ENDER_PEARL)
                    .define('G', Items.HOPPER)
                    .define('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .define('C', Items.DROPPER)
                    .save(consumer);
        }
    }
}
