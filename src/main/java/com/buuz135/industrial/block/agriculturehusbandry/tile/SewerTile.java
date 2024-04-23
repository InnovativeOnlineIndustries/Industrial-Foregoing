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

package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.SewerConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class SewerTile extends IndustrialAreaWorkingTile<SewerTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    public SidedFluidTankComponent<SewerTile> sewage;
    @Save
    public SidedFluidTankComponent<SewerTile> essence;

    public SewerTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.SEWER, RangeManager.RangeType.TOP, true, SewerConfig.powerPerOperation, blockPos, blockState);
        this.addTank(sewage = (SidedFluidTankComponent<SewerTile>) new SidedFluidTankComponent<SewerTile>("sewage", SewerConfig.maxSewageTankSize, 45, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this));
        this.addTank(essence = (SidedFluidTankComponent<SewerTile>) new SidedFluidTankComponent<SewerTile>("essence", SewerConfig.maxEssenceTankSize, 66, 20, 1).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this));
        this.maxProgress = SewerConfig.maxProgress;
        this.powerPerOperation = SewerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        List<Animal> entities = this.level.getEntitiesOfClass(Animal.class, getWorkingArea().bounds());
        int amount = entities.size();
        if (amount > 0 && hasEnergy(powerPerOperation * amount)) {
            sewage.fillForced(new FluidStack(ModuleCore.SEWAGE.getSourceFluid().get(), 50 * amount), IFluidHandler.FluidAction.EXECUTE);
            ++amount;
        }
        List<ExperienceOrb> orb = this.level.getEntitiesOfClass(ExperienceOrb.class, getWorkingArea().bounds());
        for (ExperienceOrb experienceOrbEntity : orb) {
            if (experienceOrbEntity.isAlive() && essence.getFluidAmount() + experienceOrbEntity.value * 20 <= essence.getCapacity()) {
                essence.fillForced(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), experienceOrbEntity.value * 20), IFluidHandler.FluidAction.EXECUTE);
                experienceOrbEntity.remove(Entity.RemovalReason.KILLED);
            }
        }
        return new WorkAction(1, powerPerOperation * amount);
    }

    @Override
    protected EnergyStorageComponent<SewerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SewerConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public SewerTile getSelf() {
        return this;
    }
}
