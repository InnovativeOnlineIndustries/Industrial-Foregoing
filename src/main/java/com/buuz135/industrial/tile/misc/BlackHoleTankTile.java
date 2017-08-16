package com.buuz135.industrial.tile.misc;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class BlackHoleTankTile extends SidedTileEntity {

    private IFluidTank tank;

    public BlackHoleTankTile() {
        super(BlackHoleTankTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = new FluidTank(Integer.MAX_VALUE) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                BlackHoleTankTile.this.markDirty();
            }
        };
        this.addFluidTank(tank, EnumDyeColor.CYAN, "Tank", new BoundingRectangle(10, 25, 18, 54));
    }

    @Override
    protected void innerUpdate() {

    }
}
