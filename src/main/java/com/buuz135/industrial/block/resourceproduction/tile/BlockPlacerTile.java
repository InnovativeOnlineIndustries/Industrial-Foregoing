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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.BlockPlacerConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class BlockPlacerTile extends IndustrialAreaWorkingTile<BlockPlacerTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedInventoryComponent<BlockPlacerTile> input;

    public BlockPlacerTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.BLOCK_PLACER, RangeManager.RangeType.BEHIND, false, BlockPlacerConfig.powerPerOperation, blockPos, blockState);
        this.addInventory(this.input = (SidedInventoryComponent<BlockPlacerTile>) new SidedInventoryComponent<BlockPlacerTile>("input", 54, 22, 3 * 6, 0).
                setColor(DyeColor.BLUE).
                setRange(6, 3));
        this.getMaxProgress = BlockPlacerConfig.maxProgress;
        this.getPowerPerOperation = BlockPlacerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation) && BlockUtils.canBlockBeBroken(this.level, this.worldPosition)) {
            BlockPos pointed = getPointedBlockPos();
            if (isLoaded(pointed) && level.isEmptyBlock(pointed)) {
                for (int i = 0; i < input.getSlots(); i++) {
                    if (!input.getStackInSlot(i).isEmpty() && input.getStackInSlot(i).getItem() instanceof BlockItem) {
                        IFFakePlayer fakePlayer = (IFFakePlayer) IndustrialForegoing.getFakePlayer(level, pointed);
                        if (fakePlayer.placeBlock(this.level, pointed, input.getStackInSlot(i))) {
                            increasePointer();
                            return new WorkAction(1, getPowerPerOperation);
                        }
                    }
                }
            } else {
                increasePointer();
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<BlockPlacerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(BlockPlacerConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public BlockPlacerTile getSelf() {
        return this;
    }
}
