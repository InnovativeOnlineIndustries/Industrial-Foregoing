package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class WaterCondensatorTile extends IndustrialWorkingTile<WaterCondensatorTile> {

    @Save
    private SidedFluidTankComponent<WaterCondensatorTile> water;

    public WaterCondensatorTile() {
        super(ModuleResourceProduction.WATER_CONDENSATOR);
        this.addTank(water = (SidedFluidTankComponent<WaterCondensatorTile>) new SidedFluidTankComponent<WaterCondensatorTile>("water", 16000, 30 + 13, 20, 0).
                setColor(DyeColor.BLUE).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER)));
    }

    @Override
    public WorkAction work() {
        int water = getWaterSources();
        if (water >= 2) {
            if (hasEnergy(50)) {
                this.water.fillForced(new FluidStack(Fluids.WATER, water * 10), IFluidHandler.FluidAction.EXECUTE);
                return new WorkAction(0.1f, 50);
            } else {
                this.water.fillForced(new FluidStack(Fluids.WATER, water * 5), IFluidHandler.FluidAction.EXECUTE);
                return new WorkAction(0.5f, 0);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    public int getMaxProgress() {
        return 10;
    }

    private int getWaterSources() {
        int amount = 0;
        for (Direction value : Direction.values()) {
            if (!world.isAreaLoaded(this.pos.offset(value), this.pos.offset(value))) continue;
            IFluidState fluidState = this.world.getFluidState(this.pos.offset(value));
            if (fluidState.getFluid().equals(Fluids.WATER) && fluidState.isSource()) {
                ++amount;
            }
        }
        return amount;
    }

    @Nonnull
    @Override
    public WaterCondensatorTile getSelf() {
        return this;
    }
}
