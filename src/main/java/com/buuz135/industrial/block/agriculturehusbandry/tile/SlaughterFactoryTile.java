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
import com.buuz135.industrial.config.machine.agriculturehusbandry.SlaughterFactoryConfig;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class SlaughterFactoryTile extends IndustrialAreaWorkingTile<SlaughterFactoryTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedFluidTankComponent<SlaughterFactoryTile> meat;
    @Save
    private SidedFluidTankComponent<SlaughterFactoryTile> pinkSlime;

    public SlaughterFactoryTile() {
        super(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY, RangeManager.RangeType.BEHIND, true, SlaughterFactoryConfig.powerPerOperation);
        addTank(meat = (SidedFluidTankComponent<SlaughterFactoryTile>) new SidedFluidTankComponent<SlaughterFactoryTile>("meat", SlaughterFactoryConfig.maxMeatTankSize, 43, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()))
        );
        addTank(pinkSlime = (SidedFluidTankComponent<SlaughterFactoryTile>) new SidedFluidTankComponent<SlaughterFactoryTile>("pink_slime", SlaughterFactoryConfig.maxPinkSlimeTankSize, 63, 20, 1).
                setColor(DyeColor.PINK).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.PINK_SLIME.getSourceFluid())));
        this.maxProgress = SlaughterFactoryConfig.maxProgress;
        this.powerPerOperation = SlaughterFactoryConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            List<MobEntity> mobs = this.world.getEntitiesWithinAABB(MobEntity.class, getWorkingArea().getBoundingBox()).stream().filter(LivingEntity::isAlive).collect(Collectors.toList());
            if (mobs.size() > 0) {
                MobEntity entity = mobs.get(0);
                float currentHealth = entity.getHealth();
                entity.remove(true);
                if (!entity.isAlive()) {
                    meat.fillForced(new FluidStack(ModuleCore.MEAT.getSourceFluid(), entity instanceof AnimalEntity ? (int) (currentHealth) : (int) currentHealth * 20), IFluidHandler.FluidAction.EXECUTE);
                    pinkSlime.fillForced(new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), entity instanceof AnimalEntity ? (int) (currentHealth * 20) : (int) currentHealth), IFluidHandler.FluidAction.EXECUTE);
                    return new WorkAction(0.2f, powerPerOperation);
                }
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<SlaughterFactoryTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SlaughterFactoryConfig.maxStoredPower, 10, 20);
    }

    public VoxelShape getWorkingArea() {
        return new RangeManager(this.pos, this.getFacingDirection(), RangeManager.RangeType.BEHIND){
            @Override
            public AxisAlignedBB getBox() {
                return super.getBox().expand(0,2, 0);
            }
        }.get(hasAugmentInstalled(RangeAddonItem.RANGE) ? ((int) AugmentWrapper.getType(getInstalledAugments(RangeAddonItem.RANGE).get(0), RangeAddonItem.RANGE) + 1) : 0);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public SlaughterFactoryTile getSelf() {
        return this;
    }
}
