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
package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.FluidCollectorConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidCollectorTile extends IndustrialAreaWorkingTile<FluidCollectorTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<FluidCollectorTile> tank;

    public FluidCollectorTile() {
        super(ModuleResourceProduction.FLUID_COLLECTOR, RangeManager.RangeType.BEHIND, false,FluidCollectorConfig.powerPerOperation);
        this.addTank(this.tank = (SidedFluidTankComponent<FluidCollectorTile>) new SidedFluidTankComponent<FluidCollectorTile>("output", FluidCollectorConfig.maxOutputTankSize, 43, 20, 0)
                .setColor(DyeColor.ORANGE)
                .setTankAction(FluidTankComponent.Action.DRAIN)
                .setComponentHarness(this)
        );
        this.getMaxProgress = FluidCollectorConfig.maxProgress;
        this.getPowerPerOperation = FluidCollectorConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            if (isLoaded(getPointedBlockPos()) && !world.isAirBlock(getPointedBlockPos()) && BlockUtils.canBlockBeBroken(this.world, getPointedBlockPos()) && world.getFluidState(getPointedBlockPos()).isSource()) {
                Fluid fluid = world.getFluidState(getPointedBlockPos()).getFluid();
                if (tank.isEmpty() || (tank.getFluid().getFluid().isEquivalentTo(fluid) && tank.getFluidAmount() + FluidAttributes.BUCKET_VOLUME <= tank.getCapacity())) {
                    if (world.getBlockState(getPointedBlockPos()).hasProperty(BlockStateProperties.WATERLOGGED)) { //has
                        world.setBlockState(getPointedBlockPos(), world.getBlockState(getPointedBlockPos()).with(BlockStateProperties.WATERLOGGED, false));
                    } else {
                        world.setBlockState(getPointedBlockPos(), Blocks.AIR.getDefaultState());
                    }
                    tank.fillForced(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    increasePointer();
                    return new WorkAction(1, getPowerPerOperation);
                }
            }
        }
        increasePointer();
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<FluidCollectorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(FluidCollectorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public FluidCollectorTile getSelf() {
        return this;
    }
}
