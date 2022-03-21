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
import com.buuz135.industrial.config.machine.resourceproduction.SludgeRefinerConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SludgeRefinerTile extends IndustrialProcessingTile<SludgeRefinerTile> {

    private int getPowerPerTick;

    @Save
    private SidedFluidTankComponent<SludgeRefinerTile> sludge;
    @Save
    private SidedInventoryComponent<SludgeRefinerTile> output;

    public SludgeRefinerTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.SLUDGE_REFINER, 53, 40, blockPos, blockState);
        addTank(sludge = (SidedFluidTankComponent<SludgeRefinerTile>) new SidedFluidTankComponent<SludgeRefinerTile>("sludge", SludgeRefinerConfig.maxSludgeTankSize, 31, 20, 0)
                .setColor(DyeColor.MAGENTA)
                .setComponentHarness(this)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.SLUDGE.getSourceFluid().get()))
        );
        addInventory(output = (SidedInventoryComponent<SludgeRefinerTile>) new SidedInventoryComponent<SludgeRefinerTile>("output", 80, 22, 5 * 3, 1)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setInputFilter((stack, integer) -> false)
                .setComponentHarness(this)
        );
        this.getPowerPerTick = SludgeRefinerConfig.powerPerTick;
    }

    @Override
    public boolean canIncrease() {
        return sludge.getFluidAmount() >= 500;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            Optional<Item> optionalItem = ForgeRegistries.ITEMS.tags().getTag(IndustrialTags.Items.SLUDGE_OUTPUT).getRandomElement(this.level.random);
            optionalItem.ifPresent(item -> {
                if (ItemHandlerHelper.insertItem(output, new ItemStack(item), true).isEmpty()) {
                    sludge.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
                    ItemHandlerHelper.insertItem(output, new ItemStack(item), false);
                }
            });
        };
    }

    @Override
    protected EnergyStorageComponent<SludgeRefinerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SludgeRefinerConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return getPowerPerTick;
    }

    @Nonnull
    @Override
    public SludgeRefinerTile getSelf() {
        return this;
    }
}
