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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
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
                        if (itemHandler.getStackInSlot(i).isItemEqual(stack)) {
                            amount += itemHandler.getStackInSlot(i).getCount();
                        }
                    }
                }

                for (IFilter.GhostSlot slot : this.getFilter()) {
                    if (stack.isItemEqual(slot.getStack())) {
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
        if (!getWorld().isRemote && getWorld().getGameTime() % (Math.max(1, 4 - speed)) == 0) {
            IBlockContainer container = getContainer();
            if (getAction() == TransporterTypeFactory.TransporterAction.EXTRACT && container instanceof TransporterTile) {
                for (Direction direction : ((TransporterTile) container).getTransporterTypeMap().keySet()) {
                    TransporterType transporterType = ((TransporterTile) container).getTransporterTypeMap().get(direction);
                    if (transporterType instanceof TransporterItemType && transporterType.getAction() == TransporterTypeFactory.TransporterAction.INSERT) {
                        TileUtil.getTileEntity(getWorld(), getPos().offset(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                            TileUtil.getTileEntity(getWorld(), getPos().offset(direction)).ifPresent(otherTile -> otherTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(destination -> {
                                if (origin.getStackInSlot(extractSlot).isEmpty()
                                        || !filter(this.getFilter(), this.isWhitelist(), origin.getStackInSlot(extractSlot), origin, false)
                                        || !filter(((TransporterItemType) transporterType).getFilter(), ((TransporterItemType) transporterType).isWhitelist(), origin.getStackInSlot(extractSlot), destination, ((TransporterItemType) transporterType).isRegulated()))
                                    findSlot(origin, ((TransporterItemType) transporterType).getFilter(), ((TransporterItemType) transporterType).isWhitelist(), destination, ((TransporterItemType) transporterType).isRegulated());
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
    public void handleRenderSync(Direction origin, CompoundNBT compoundNBT) {
        this.queue.computeIfAbsent(origin, direction -> new ArrayList<>()).add(0, ItemStack.read(compoundNBT));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, MatrixStack stack, int combinedOverlayIn, IRenderTypeBuffer buffer) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer);
        if (step < queue.computeIfAbsent(direction, v -> new ArrayList<>()).size()) {
            float scale = 0.10f;
            stack.scale(scale, scale, scale);
            ItemStack itemStack = queue.get(direction).get(step);
            if (!itemStack.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, 0xF000F0, combinedOverlayIn, stack, buffer);
            } else {
                stack.push();
                stack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
                stack.rotate(Vector3f.ZP.rotationDegrees(90f));
                stack.rotate(Vector3f.XP.rotationDegrees(90f));
                IVertexBuilder buffer1 = buffer.getBuffer(TransporterTESR.TYPE);
                Matrix4f matrix = stack.getLast().getMatrix();
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
                int alpha = 512;
                stack.scale(0.25f, 0.25f, 0.25f);
                int red = (int) Math.abs((ratio * FAR.getRed()) + ((1 - ratio) * CLOSE.getRed()));
                int green = (int) Math.abs((ratio * FAR.getGreen()) + ((1 - ratio) * CLOSE.getGreen()));
                int blue = (int) Math.abs((ratio * FAR.getBlue()) + ((1 - ratio) * CLOSE.getBlue()));
                buffer1.pos(matrix, pX2 + xOffset, yOffset, 0 + zOffset).tex(u2, 0).color(red, green, blue, alpha).endVertex();
                buffer1.pos(matrix, pX1 + xOffset + 0.5f, yOffset, 0 + zOffset).tex(u, 0).color(red, green, blue, alpha).endVertex();
                buffer1.pos(matrix, pX1 + xOffset + 0.5f, yOffset, 1.5f + zOffset).tex(u, 1).color(red, green, blue, alpha).endVertex();
                buffer1.pos(matrix, pX2 + xOffset, yOffset, 1.5f + zOffset).tex(u2, 1).color(red, green, blue, alpha).endVertex();
                stack.pop();
            }
        }
    }

    public static class Factory extends TransporterTypeFactory {

        public Factory() {
            setRegistryName("item");
        }

        @Override
        public TransporterType create(IBlockContainer container, Direction face, TransporterAction action) {
            return new TransporterItemType(container, this, face, action);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, TransporterAction action) {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/item_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getString().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(new ResourceLocation("industrialforegoing:blocks/transporters/item"), new ResourceLocation("industrialforegoing:blocks/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(World world, BlockPos pos, Direction face) {
            return TileUtil.getTileEntity(world, pos).map(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).isPresent()).orElse(false);
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/item_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getString().toLowerCase());
        }

        @Override
        public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem(), 2)
                    .patternLine("IPI").patternLine("GMG").patternLine("ICI")
                    .key('I', Tags.Items.DUSTS_REDSTONE)
                    .key('P', Items.ENDER_PEARL)
                    .key('G', Tags.Items.INGOTS_GOLD)
                    .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .key('C', Items.PISTON)
                    .build(consumer);
        }
    }
}
