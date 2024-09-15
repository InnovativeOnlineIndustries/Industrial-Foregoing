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
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidCollectorTile extends IndustrialAreaWorkingTile<FluidCollectorTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<FluidCollectorTile> tank;

    public FluidCollectorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.FLUID_COLLECTOR, RangeManager.RangeType.BEHIND, false, FluidCollectorConfig.powerPerOperation, blockPos, blockState);
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
            BlockPos pointed = getPointedBlockPos();
            if (isLoaded(pointed) && !level.isEmptyBlock(pointed) && BlockUtils.canBlockBeBroken(this.level, pointed, this.getUuid()) && level.getFluidState(pointed).isSource()) {
                Fluid fluid = level.getFluidState(pointed).getType();
                if (tank.isEmpty() || (tank.getFluid().getFluid().isSame(fluid) && tank.getFluidAmount() + FluidType.BUCKET_VOLUME <= tank.getCapacity())) {
                    if (level.getBlockState(pointed).hasProperty(BlockStateProperties.WATERLOGGED)) { //has
                        level.setBlockAndUpdate(pointed, level.getBlockState(pointed).setValue(BlockStateProperties.WATERLOGGED, false));
                    } else {
                        level.setBlockAndUpdate(pointed, Blocks.AIR.defaultBlockState());
                    }
                    tank.fillForced(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
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
