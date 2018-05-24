package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ConveyorExtractionUpgrade extends ConveyorUpgrade {

    public ConveyorExtractionUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        super(container, factory, side);
    }

    public static Cuboid NORTHBB = new Cuboid(0.0625 * 4, 0.0625*3, -0.0625 * 2, 0.0625 * 12, 0.0625 * 11, 0.0625 * 3,EnumFacing.NORTH.getIndex());
    public static Cuboid SOUTHBB = new Cuboid(0.0625 * 4, 0.0625*3, 0.0625*13, 0.0625 * 12, 0.0625 * 11, 0.0625*18,EnumFacing.SOUTH.getIndex());
    public static Cuboid EASTBB = new Cuboid(0.0625*13, 0.0625*3, 0.0625 * 4, 0.0625*18, 0.0625 * 11, 0.0625 * 12,EnumFacing.EAST.getIndex());
    public static Cuboid WESTBB = new Cuboid(-0.0625 * 2, 0.0625*3, 0.0625 * 4, 0.0625 * 3, 0.0625 * 11, 0.0625 * 12,EnumFacing.WEST.getIndex());

    @Override
    public Cuboid getBoundingBox() {
        switch (getSide()) {
            default:
            case NORTH:
                return NORTHBB;
            case SOUTH:
                return SOUTHBB;
            case EAST:
                return EASTBB;
            case WEST:
                return WESTBB;
        }
    }

    @Override
    public void update() {
        if(getWorld().isRemote)
            return;
        if (getWorld().getTotalWorldTime() % 10 == 0) {
            BlockPos offsetPos = getPos().offset(getSide());
            TileEntity tile = getWorld().getTileEntity(offsetPos);
            if (tile != null) {
                IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite());
                if (itemHandler != null) {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        ItemStack stack = itemHandler.extractItem(i, 4, true);
                        if (stack.isEmpty())
                            continue;
                        EntityItem item = new EntityItem(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
                        item.motionX = 0;
                        item.motionY = 0;
                        item.motionZ = 0;
                        item.setItem(itemHandler.extractItem(i, 4, false));
                        getWorld().spawnEntity(item);
                        break;
                    }
                }
            }
        }
    }


    public static class Factory extends ConveyorUpgradeFactory {
        public Factory() {
            setRegistryName("extraction");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, EnumFacing face) {
            return new ConveyorExtractionUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(EnumFacing facing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_extractor_"+facing.getName().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID,"conveyor_extraction_upgrade");
        }
    }
}