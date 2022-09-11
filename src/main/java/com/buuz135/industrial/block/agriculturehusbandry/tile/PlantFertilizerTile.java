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
import com.buuz135.industrial.config.machine.agriculturehusbandry.PlantFertilizerConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.ItemHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class PlantFertilizerTile extends IndustrialAreaWorkingTile<PlantFertilizerTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    public SidedInventoryComponent<PlantFertilizerTile> fertilizer;

    public PlantFertilizerTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.PLANT_FERTILIZER, RangeManager.RangeType.BEHIND, true, PlantFertilizerConfig.powerPerOperation, blockPos, blockState);
        addInventory(fertilizer = (SidedInventoryComponent<PlantFertilizerTile>) new SidedInventoryComponent<PlantFertilizerTile>("fertilizer", 50, 22, 3 * 6, 0).
                setColor(DyeColor.BROWN).
                setInputFilter((stack, integer) -> stack.is(IndustrialTags.Items.FERTILIZER)).
                setOutputFilter((stack, integer) -> false).
                setRange(6, 3).
                setComponentHarness(this)
        );
        this.maxProgress = PlantFertilizerConfig.maxProgress;
        this.powerPerOperation = PlantFertilizerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        ItemStack stack = ItemHandlerUtil.getFirstItem(fertilizer);
        if (!stack.isEmpty() && hasEnergy(powerPerOperation)) {
            BlockPos pointer = getPointedBlockPos();
            if (isLoaded(pointer)) {
                BlockState state = this.level.getBlockState(pointer);
                Block block = state.getBlock();
                if (block instanceof BonemealableBlock) {
                    if (((BonemealableBlock) block).isValidBonemealTarget(level, pointer, state, false) && ((BonemealableBlock) block).isBonemealSuccess(level, level.random, pointer, state)) {
                        stack.shrink(1);
                        ((BonemealableBlock) block).performBonemeal((ServerLevel) level, level.random, pointer, state);
                        if (((BonemealableBlock) block).isValidBonemealTarget(level, pointer, state, false)) {
                            return new WorkAction(0.25f, powerPerOperation);
                        } else {
                            increasePointer();
                            return new WorkAction(0.5f, powerPerOperation);
                        }
                    } else {
                        increasePointer();
                        return new WorkAction(0.5f, 0);
                    }
                }
            }
        }
        increasePointer();
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<PlantFertilizerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(PlantFertilizerConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public PlantFertilizerTile getSelf() {
        return this;
    }
}
