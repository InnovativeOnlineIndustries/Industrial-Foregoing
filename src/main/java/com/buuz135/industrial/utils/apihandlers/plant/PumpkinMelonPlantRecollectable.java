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
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public class PumpkinMelonPlantRecollectable extends PlantRecollectable {

    public PumpkinMelonPlantRecollectable() {
        super("blockpumpkingandmelon");
    }

    @Override
    public boolean canBeHarvested(Level world, BlockPos pos, BlockState blockState) {
        return blockState.getBlock() instanceof PumpkinBlock || blockState.getBlock().equals(Blocks.MELON) || blockState.getBlock() instanceof AttachedStemBlock;
    }

    @Override
    public List<ItemStack> doHarvestOperation(Level world, BlockPos pos, BlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (blockState.getBlock() instanceof AttachedStemBlock) {
            stacks.addAll(BlockUtils.getBlockDrops(world, pos.relative(blockState.getValue(AttachedStemBlock.FACING))));
            world.setBlockAndUpdate(pos.relative(blockState.getValue(AttachedStemBlock.FACING)), Blocks.AIR.defaultBlockState());
        } else {
            stacks.addAll(BlockUtils.getBlockDrops(world, pos));
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(Level world, BlockPos pos, BlockState blockState) {
        return true;
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.pumpkin", "text.industrialforegoing.plant.melon");
    }

    @Override
    public ItemStack getSeedDrop(Level world, BlockPos pos, BlockState blockState) {
        if (blockState.getBlock() instanceof AttachedStemBlock) {
            return blockState.getBlock().getCloneItemStack(world, pos, blockState);
        }
        return super.getSeedDrop(world, pos, blockState);
    }
}
