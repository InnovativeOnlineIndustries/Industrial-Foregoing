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

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.agriculturehusbandry.SewageComposterConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SewageComposterTile extends IndustrialProcessingTile<SewageComposterTile> {

    private int maxProgress;
    private int powerPerTick;

    @Save
    public SidedFluidTankComponent<SewageComposterTile> sewage;
    @Save
    public SidedInventoryComponent<SewageComposterTile> fertilizerOutput;

    public SewageComposterTile() {
        super(ModuleAgricultureHusbandry.SEWAGE_COMPOSTER, 57, 40);
        this.addTank(sewage = (SidedFluidTankComponent<SewageComposterTile>) new SidedFluidTankComponent<SewageComposterTile>("sewage", SewageComposterConfig.maxTankSize, 30, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.SEWAGE.getSourceFluid())));
        this.addInventory(fertilizerOutput = (SidedInventoryComponent<SewageComposterTile>) new SidedInventoryComponent<SewageComposterTile>("fertilizer", 90, 22, 12, 1).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setRange(4, 3).
                setComponentHarness(this));
        this.maxProgress = SewageComposterConfig.maxProgress;
        this.powerPerTick = SewageComposterConfig.powerPerTick;
    }

    @Override
    public boolean canIncrease() {
        return sewage.getFluidAmount() >= 1000 && ItemHandlerHelper.insertItem(fertilizerOutput, new ItemStack(ModuleCore.FERTILIZER), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            sewage.drainForced(1000, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerHelper.insertItem(fertilizerOutput, new ItemStack(ModuleCore.FERTILIZER), false);
        };
    }

    @Override
    protected EnergyStorageComponent<SewageComposterTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SewageComposterConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    protected int getTickPower() {
        return powerPerTick;
    }

    @Nonnull
    @Override
    public SewageComposterTile getSelf() {
        return this;
    }
}
