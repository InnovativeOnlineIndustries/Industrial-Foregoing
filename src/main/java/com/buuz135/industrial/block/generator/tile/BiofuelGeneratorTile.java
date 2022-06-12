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

package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.config.machine.generator.BiofuelGeneratorConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class BiofuelGeneratorTile extends IndustrialGeneratorTile<BiofuelGeneratorTile> {

    private int getPowerPerTick;
    private int getExtractionRate;

    @Save
    private SidedFluidTankComponent<BiofuelGeneratorTile> biofuel;

    public BiofuelGeneratorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleGenerator.BIOFUEL_GENERATOR, blockPos, blockState);
        addTank(biofuel = (SidedFluidTankComponent<BiofuelGeneratorTile>) new SidedFluidTankComponent<BiofuelGeneratorTile>("biofuel", BiofuelGeneratorConfig.maxBiofuelTankSize, 43, 20, 0).
                setColor(DyeColor.PURPLE).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.BIOFUEL.getSourceFluid().get()))
        );
        this.getPowerPerTick = BiofuelGeneratorConfig.powerPerTick;
        this.getExtractionRate = BiofuelGeneratorConfig.extractionRate;
    }

    @Override
    public int consumeFuel() {
        if (biofuel.getFluidAmount() > 0) {
            biofuel.drainForced(1, IFluidHandler.FluidAction.EXECUTE);
            return 4;
        }
        return 0;
    }

    @Override
    public boolean canStart() {
        return biofuel.getFluidAmount() > 0 && this.getEnergyStorage().getEnergyStored() < this.getEnergyStorage().getMaxEnergyStored();
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return getPowerPerTick;
    }

    @Override
    public ProgressBarComponent getProgressBar() {
        return new ProgressBarComponent(30, 20, 0, BiofuelGeneratorConfig.maxProgress)
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return BiofuelGeneratorConfig.maxStoredPower;
    }

    @Override
    public int getExtractingEnergy() {
        return BiofuelGeneratorConfig.extractionRate;
    }

    @Nonnull
    @Override
    public BiofuelGeneratorTile getSelf() {
        return this;
    }
}
