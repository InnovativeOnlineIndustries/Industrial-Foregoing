package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.tile.CustomGeneratorMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public abstract class AbstractReactorGeneratorTile extends CustomGeneratorMachine {

    private IFluidTank tank;
    private int generatedPower;

    public AbstractReactorGeneratorTile(int id, int generatedPower) {
        super(id);
        this.generatedPower = generatedPower;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(getFluid(), 8000, EnumDyeColor.PURPLE, "Tank", new BoundingRectangle(58, 25, 18, 54));
    }

    @Override
    protected long consumeFuel() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (tank.getFluid() != null && tank.getFluidAmount() >= 1) {
            tank.drain(1, true);
            return generatedPower * 7L;
        }
        return 0;
    }

    @Override
    public long getEnergyFillRate() {
        return generatedPower;
    }

    @Override
    protected long getMaxEnergy() {
        return 1000000;
    }

    public abstract Fluid getFluid();
}
