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
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

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
                TileUtil.getTileEntity(getWorld(), getPos().offset(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                    if (origin.getStackInSlot(extractSlot).isEmpty() || !filter(this.getFilter(), this.isWhitelist(), origin.getStackInSlot(extractSlot), origin, false))
                        findSlot(origin);
                    if (!origin.getStackInSlot(extractSlot).isEmpty() && filter(this.getFilter(), this.isWhitelist(), origin.getStackInSlot(extractSlot), origin, false)) {
                        int amount = (int) (1 * getEfficiency());
                        ItemStack extracted = origin.extractItem(extractSlot, amount, false);
                        if (extracted.isEmpty()) return;
                        ItemEntity item = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.2, getPos().getZ() + 0.5);
                        item.setMotion(0, 0, 0);
                        item.setPickupDelay(4);
                        item.setItem(extracted);
                        getWorld().addEntity(item);
                    }
                }));
            }
            if (getAction() == TransporterTypeFactory.TransporterAction.INSERT && container instanceof TransporterTile) {
                TileUtil.getTileEntity(getWorld(), getPos().offset(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                    for (ItemEntity item : this.getWorld().getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1))) {
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
                                            item.remove();
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
    public void handleRenderSync(Direction origin, CompoundNBT compoundNBT) {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, MatrixStack stack, int combinedOverlayIn, IRenderTypeBuffer buffer) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer);

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
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/world_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getString().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(new ResourceLocation("industrialforegoing:blocks/transporters/world"), new ResourceLocation("industrialforegoing:blocks/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(World world, BlockPos pos, Direction face) {
            return TileUtil.getTileEntity(world, pos).map(tileEntity -> tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).isPresent()).orElse(false);
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/world_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getString().toLowerCase());
        }

        @Override
        public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem(), 2)
                    .patternLine("IPI").patternLine("GMG").patternLine("ICI")
                    .key('I', Tags.Items.DUSTS_REDSTONE)
                    .key('P', Items.ENDER_PEARL)
                    .key('G', Items.HOPPER)
                    .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .key('C', Items.DROPPER)
                    .build(consumer);
        }
    }
}
