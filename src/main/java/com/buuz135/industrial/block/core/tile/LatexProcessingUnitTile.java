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
package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.core.LatexProcessingUnitConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class LatexProcessingUnitTile extends IndustrialProcessingTile<LatexProcessingUnitTile> {

    private static int AMOUNT_LATEX = 100;
    private static int AMOUNT_WATER = 500;

    @Save
    private SidedFluidTankComponent<LatexProcessingUnitTile> latex;
    @Save
    private SidedFluidTankComponent<LatexProcessingUnitTile> water;
    @Save
    private SidedInventoryComponent<LatexProcessingUnitTile> output;

    public LatexProcessingUnitTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<LatexProcessingUnitTile>) ModuleCore.LATEX_PROCESSING.get(), 48 + 25, 40, blockPos, blockState);
        this.addTank(latex = (SidedFluidTankComponent<LatexProcessingUnitTile>) new SidedFluidTankComponent<LatexProcessingUnitTile>("latex", LatexProcessingUnitConfig.maxLatexTankSize, 29, 20, 0).
                setColor(DyeColor.LIGHT_GRAY).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.LATEX.getSourceFluid())));
        this.addTank(water = (SidedFluidTankComponent<LatexProcessingUnitTile>) new SidedFluidTankComponent<LatexProcessingUnitTile>("water", LatexProcessingUnitConfig.maxWaterTankSize, 30 + 18, 20, 1).
                setColor(DyeColor.BLUE).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isSame(Fluids.WATER)));
        this.addInventory(output = (SidedInventoryComponent<LatexProcessingUnitTile>) new SidedInventoryComponent<LatexProcessingUnitTile>("output", 70 + 34, 22, 9, 2).
                setColor(DyeColor.ORANGE).
                setRange(3, 3).
                setComponentHarness(this));
    }

    @Override
    public boolean canIncrease() {
        return latex.getFluidAmount() >= AMOUNT_LATEX && water.getFluidAmount() >= AMOUNT_WATER && ItemHandlerHelper.insertItem(output, new ItemStack(ModuleCore.TINY_DRY_RUBBER.get()), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            latex.drain(AMOUNT_LATEX, IFluidHandler.FluidAction.EXECUTE);
            water.drain(AMOUNT_WATER, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerHelper.insertItem(output, new ItemStack(ModuleCore.TINY_DRY_RUBBER.get()), false);
        };
    }

    @Override
    protected EnergyStorageComponent<LatexProcessingUnitTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(LatexProcessingUnitConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return LatexProcessingUnitConfig.powerPerTick;
    }

    @Nonnull
    @Override
    public LatexProcessingUnitTile getSelf() {
        return this;
    }
}
