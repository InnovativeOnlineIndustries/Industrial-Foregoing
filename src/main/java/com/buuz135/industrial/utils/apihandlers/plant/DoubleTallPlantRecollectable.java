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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class DoubleTallPlantRecollectable extends PlantRecollectable {

    public DoubleTallPlantRecollectable() {
        super("blocksugarandcactus");
    }

    @Override
    public boolean canBeHarvested(Level world, BlockPos pos, BlockState blockState) {
        return (blockState.getBlock() instanceof CactusBlock && world.getBlockState(pos.above()).getBlock() instanceof CactusBlock)
                || (blockState.getBlock() instanceof SugarCaneBlock && world.getBlockState(pos.above()).getBlock() instanceof SugarCaneBlock);
    }

    @Override
    public List<ItemStack> doHarvestOperation(Level world, BlockPos pos, BlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        harvestBlock(stacks, world, pos.relative(Direction.UP, 2));
        harvestBlock(stacks, world, pos.relative(Direction.UP, 1));
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(Level world, BlockPos pos, BlockState blockState) {
        return true;
    }

    private void harvestBlock(NonNullList<ItemStack> stacks, Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof CactusBlock || blockState.getBlock() instanceof SugarCaneBlock) {
            stacks.addAll(BlockUtils.getBlockDrops(world, pos));
            if (!world.getFluidState(pos).isEmpty()) {
                world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            } else {
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.sugar_cane", "text.industrialforegoing.plant.cactus");
    }
}
