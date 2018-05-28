package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.gui.component.FilterGuiComponent;
import com.buuz135.industrial.gui.component.IGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ConveyorInsertionUpgrade extends ConveyorUpgrade {

    public static Cuboid NORTHBB = new Cuboid(0.0625 * 2, 0, -0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 2, EnumFacing.NORTH.getIndex());
    public static Cuboid SOUTHBB = new Cuboid(0.0625 * 2, 0, 0.0625 * 14, 0.0625 * 14, 0.0625 * 9, 0.0625 * 18, EnumFacing.SOUTH.getIndex());
    public static Cuboid EASTBB = new Cuboid(0.0625 * 14, 0, 0.0625 * 2, 0.0625 * 18, 0.0625 * 9, 0.0625 * 14, EnumFacing.EAST.getIndex());
    public static Cuboid WESTBB = new Cuboid(-0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 2, 0.0625 * 9, 0.0625 * 14, EnumFacing.WEST.getIndex());

    public static Cuboid NORTHBB_BIG = new Cuboid(0.0625 * 2, 0, -0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 14, EnumFacing.NORTH.getIndex());
    public static Cuboid SOUTHBB_BIG = new Cuboid(0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 18, EnumFacing.SOUTH.getIndex());
    public static Cuboid EASTBB_BIG = new Cuboid(0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 18, 0.0625 * 9, 0.0625 * 14, EnumFacing.EAST.getIndex());
    public static Cuboid WESTBB_BIG = new Cuboid(-0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 14, EnumFacing.WEST.getIndex());

    private ItemStackFilter filter;
    private boolean whitelist;
    private boolean fullArea;

    public ConveyorInsertionUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = true;
        this.fullArea = false;
    }

    @Override
    public void handleEntity(Entity entity) {
        if (getWorld().isRemote)
            return;
        if (entity instanceof EntityItem) {
            TileEntity tile = getWorld().getTileEntity(getPos().offset(getSide()));
            if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite())) {
                IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getSide().getOpposite());
                if (getWorkingBox().aabb().offset(getPos()).grow(0.01).intersects(entity.getEntityBoundingBox())) {
                    if (whitelist != filter.matches((EntityItem) entity)) return;
                    ItemStack stack = ((EntityItem) entity).getItem();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack remaining = handler.insertItem(i, stack, false);
                        if (remaining.isEmpty()) {
                            entity.setDead();
                            break;
                        } else {
                            ((EntityItem) entity).setItem(remaining);
                        }
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT() == null ? new NBTTagCompound() : super.serializeNBT();
        compound.setTag("Filter", filter.serializeNBT());
        compound.setBoolean("Whitelist", whitelist);
        compound.setBoolean("FullArea", fullArea);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey("Filter")) filter.deserializeNBT(nbt.getCompoundTag("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
        fullArea = nbt.getBoolean("FullArea");
    }

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

    private Cuboid getWorkingBox() {
        if (!fullArea) return getBoundingBox();
        switch (getSide()) {
            default:
            case NORTH:
                return NORTHBB_BIG;
            case SOUTH:
                return SOUTHBB_BIG;
            case EAST:
                return EASTBB_BIG;
            case WEST:
                return WESTBB_BIG;
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void handleButtonInteraction(int buttonId, NBTTagCompound compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            this.filter.setFilter(buttonId, new ItemStack(compound));
            this.getContainer().requestSync();
        }
        if (buttonId == 16) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
        if (buttonId == 17) {
            fullArea = !fullArea;
            this.getContainer().requestSync();
        }
    }

    @Override
    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new FilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public IFilter getFilter() {
                return ConveyorInsertionUpgrade.this.filter;
            }
        });
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TexturedStateButtonGuiComponent(16, 133, 20, 18, 18,
                new StateButtonInfo(0, res, 1, 214, new String[]{"whitelist"}),
                new StateButtonInfo(1, res, 20, 214, new String[]{"blacklist"})) {
            @Override
            public int getState() {
                return whitelist ? 0 : 1;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(17, 133, 20 + 35, 18, 18,
                new StateButtonInfo(0, res, 39, 214, new String[]{"insert_near"}),
                new StateButtonInfo(1, res, 58, 214, new String[]{"insert_all"})) {
            @Override
            public int getState() {
                return fullArea ? 1 : 0;
            }
        });
    }

    public static class Factory extends ConveyorUpgradeFactory {
        public Factory() {
            setRegistryName("insertion");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, EnumFacing face) {
            return new ConveyorInsertionUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(EnumFacing upgradeSide, EnumFacing conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_inserter_" + upgradeSide.getName().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_insertion_upgrade");
        }
    }
}