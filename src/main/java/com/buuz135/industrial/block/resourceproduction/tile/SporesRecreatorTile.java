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

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.SporeRecreatorConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.ItemHandlerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SporesRecreatorTile extends IndustrialProcessingTile<SporesRecreatorTile> {

    private int getPowerPerTick;

    @Save
    private SidedFluidTankComponent<SporesRecreatorTile> tank;
    @Save
    private SidedInventoryComponent<SporesRecreatorTile> input;
    @Save
    private SidedInventoryComponent<SporesRecreatorTile> output;

    public SporesRecreatorTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<SporesRecreatorTile>) ModuleResourceProduction.SPORES_RECREATOR.get(), 79, 40, blockPos, blockState);
        addTank(tank = (SidedFluidTankComponent<SporesRecreatorTile>) new SidedFluidTankComponent<SporesRecreatorTile>("water", SporeRecreatorConfig.maxWaterTankSize, 31, 20, 0).
                setColor(DyeColor.CYAN).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isSame(Fluids.WATER) || fluidStack.getFluid().isSame(Fluids.LAVA))
        );
        addInventory(input = (SidedInventoryComponent<SporesRecreatorTile>) new SidedInventoryComponent<SporesRecreatorTile>("input", 53, 22, 3, 1)
                .setColor(DyeColor.BLUE)
                .setRange(1, 3)
                .setComponentHarness(this)
                .setInputFilter((stack, integer) -> stack.is(Tags.Items.MUSHROOMS) || stack.getItem().equals(Items.CRIMSON_FUNGUS) || stack.getItem().equals(Items.WARPED_FUNGUS))
                .setOutputFilter((stack, integer) -> false)
        );
        addInventory(output = (SidedInventoryComponent<SporesRecreatorTile>) new SidedInventoryComponent<SporesRecreatorTile>("output", 110, 22, 9, 2)
                .setColor(DyeColor.ORANGE)
                .setRange(3, 3)
                .setComponentHarness(this)
                .setInputFilter((stack, integer) -> false)
        );
        this.getPowerPerTick = SporeRecreatorConfig.powerPerTick;
    }

    @Override
    public boolean canIncrease() {
        return !ItemHandlerUtil.getFirstItem(input).isEmpty() && tank.getFluidAmount() >= 100
                && (ItemHandlerUtil.getFirstItem(input).is(Tags.Items.MUSHROOMS) ?  tank.getFluid().getFluid().isSame(Fluids.WATER) : tank.getFluid().getFluid().isSame(Fluids.LAVA))
        && ItemHandlerHelper.insertItem(output, new ItemStack(ItemHandlerUtil.getFirstItem(input).getItem(), 2), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack outputStack = new ItemStack(ItemHandlerUtil.getFirstItem(input).getItem(), 2);
            tank.drainForced(100, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerUtil.getFirstItem(input).shrink(1);
            ItemHandlerHelper.insertItem(output, outputStack, false);
        };
    }

    @Override
    protected EnergyStorageComponent<SporesRecreatorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SporeRecreatorConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return getPowerPerTick;
    }

    @Nonnull
    @Override
    public SporesRecreatorTile getSelf() {
        return this;
    }
}
