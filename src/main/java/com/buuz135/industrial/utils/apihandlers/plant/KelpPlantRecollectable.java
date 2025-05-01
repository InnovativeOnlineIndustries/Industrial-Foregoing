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

package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class KelpPlantRecollectable extends PlantRecollectable {

    public KelpPlantRecollectable() {
        super("kelp");
    }

    @Override
    public boolean canBeHarvested(Level world, BlockPos pos, BlockState blockState) {
        return world.getBlockState(pos).getBlock().equals(Blocks.KELP_PLANT) && (world.getBlockState(pos.above()).getBlock().equals(Blocks.KELP) || world.getBlockState(pos.above()).getBlock().equals(Blocks.KELP_PLANT));
    }

    @Override
    public List<ItemStack> doHarvestOperation(Level world, BlockPos pos, BlockState blockState) {
        while (world.getBlockState(pos.above()).getBlock().equals(Blocks.KELP) || world.getBlockState(pos.above()).getBlock().equals(Blocks.KELP_PLANT))
            pos = pos.above();
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(BlockUtils.getBlockDrops(world, pos));
        world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(Level world, BlockPos pos, BlockState blockState) {
        return world.getBlockState(pos).getBlock().equals(Blocks.KELP);
    }

    @Override
    public ItemStack getSeedDrop(Level world, BlockPos pos, BlockState blockState) {
        return new ItemStack(Blocks.KELP);
    }
}
