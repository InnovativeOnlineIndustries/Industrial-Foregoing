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

package com.buuz135.industrial.block.tile;


import com.buuz135.industrial.capability.tile.BigEnergyHandler;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.GeneratorTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.module.BlockWithTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public abstract class IndustrialGeneratorTile<T extends IndustrialGeneratorTile<T>> extends GeneratorTile<T> {

    public IndustrialGeneratorTile(BlockWithTile basicTileBlock, BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<T>) basicTileBlock.getBlock(), basicTileBlock.type().get(), blockPos, blockState);
    }

    @Override
    public ItemInteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ItemInteractionResult.SUCCESS) {
            return ItemInteractionResult.SUCCESS;
        }
        openGui(playerIn);
        return ItemInteractionResult.SUCCESS;
    }

    @Nonnull
    @Override
    protected EnergyStorageComponent<T> createEnergyStorage() {
        return new BigEnergyHandler<T>(getEnergyCapacity(), 0, getExtractingEnergy(), 10, 20) {
            @Override
            public void sync() {
                IndustrialGeneratorTile.this.syncObject(getEnergyStorage());
            }
        };
    }

}
