package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.util.math.AxisAlignedBB;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;

public class EnergyFieldProviderTile extends WorkingAreaElectricMachine {

    public EnergyFieldProviderTile() {
        super(ElectricTileEntity.class.getName().hashCode(), 3, 3, true);
    }

    @Override
    public float work() {
        return 0;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {//(getRadius()*getRadius())/3 -1, (getRadius()*getRadius())/3 -1, (getRadius()*getRadius())/3 -1
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(getRadius() * 2, getRadius() * 2, getRadius() * 2);
    }

    public float consumeWorkEnergy(long consume) {
        return this.getEnergyStorage().takePower(consume);
    }
}
