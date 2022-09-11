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
import com.buuz135.industrial.config.machine.generator.PitifulGeneratorConfig;
import com.buuz135.industrial.module.ModuleGenerator;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

public class PitifulGeneratorTile extends IndustrialGeneratorTile<PitifulGeneratorTile> {

    private int getPowerPerTick;

    @Save
    private SidedInventoryComponent<PitifulGeneratorTile> fuel;

    public PitifulGeneratorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleGenerator.PITIFUL_GENERATOR, blockPos, blockState);
        this.addInventory(fuel = (SidedInventoryComponent<PitifulGeneratorTile>) new SidedInventoryComponent<PitifulGeneratorTile>("fuel_input", 46, 22, 1, 0)
                .setColor(DyeColor.ORANGE)
                .setInputFilter((itemStack, integer) -> ForgeHooks.getBurnTime(itemStack, RecipeType.SMELTING) != 0)
                .setComponentHarness(this)
        );
        this.getPowerPerTick = PitifulGeneratorConfig.powerPerTick;
    }

    @Override
    public int consumeFuel() {
        int time = ForgeHooks.getBurnTime(fuel.getStackInSlot(0), RecipeType.SMELTING);
        fuel.getStackInSlot(0).shrink(1);
        return time;
    }

    @Override
    public boolean canStart() {
        return !fuel.getStackInSlot(0).isEmpty() && ForgeHooks.getBurnTime(fuel.getStackInSlot(0), RecipeType.SMELTING) != 0;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return getPowerPerTick;
    }

    @Override
    public ProgressBarComponent<PitifulGeneratorTile> getProgressBar() {
        return new ProgressBarComponent<PitifulGeneratorTile>(30, 20, 0, 100)
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return PitifulGeneratorConfig.maxStoredPower;
    }

    @Override
    public int getExtractingEnergy() {
        return PitifulGeneratorConfig.extractionRate;
    }

    @Override
    public boolean isSmart() {
        return false;
    }

    @Nonnull
    @Override
    public PitifulGeneratorTile getSelf() {
        return this;
    }
}
