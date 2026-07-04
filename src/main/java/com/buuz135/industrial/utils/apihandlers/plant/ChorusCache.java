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

import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChorusCache {

    private List<BlockPos> chorus;
    private Level world;

    public ChorusCache(Level world, BlockPos current) {
        this.world = world;
        this.chorus = new ArrayList<>();
        Stack<BlockPos> chorus = new Stack<>();
        chorus.push(current);
        while (!chorus.isEmpty()) {
            BlockPos checking = chorus.pop();
            if (BlockUtils.isChorus(world, checking)) {
                Iterable<BlockPos> area = BlockPos.betweenClosed(checking.offset(-1, 0, -1), checking.offset(1, 1, 1));
                for (BlockPos blockPos : area) {
                    // Chorus can grow up to 22 blocks, tall - assuming it grows sideways each time as well, you have a
                    // max distance of sqrt(22^2 + 22^2) = 31.11...
                    // We round that up to 32 then square it to get 1024.
                    if (blockPos.distSqr(current) > 1024) {
                        continue;
                    }
                    
                    if (BlockUtils.isChorus(world, blockPos) && !this.chorus.contains(blockPos)) {
                        chorus.push(blockPos.immutable());
                        this.chorus.add(blockPos.immutable());
                    }
                }
            }
        }
    }

    public boolean isFullyGrown() {
        for (BlockPos blockpos : chorus) {
            BlockState blockState = world.getBlockState(blockpos);
            var block = blockState.getBlock();
            if (!block.equals(Blocks.CHORUS_PLANT) && (!block.equals(Blocks.CHORUS_FLOWER) || blockState.getValue(ChorusFlowerBlock.AGE) != 5)) {
                return false;
            }
        }

        return true;
    }

    public List<ItemStack> chop() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        int maxY = getTopRowY();
        if (maxY == Integer.MIN_VALUE) {
            return stacks;
        }

        var iter = chorus.listIterator();
        while (iter.hasNext()) {
            var blockPos = iter.next();
            if (blockPos.getY() == maxY) {
                chop(stacks, blockPos);
                iter.remove();
            }
        }
        return stacks;
    }

    public void chop(NonNullList<ItemStack> stacks, BlockPos p) {
        var state = world.getBlockState(p);
        var block = state.getBlock();
        if (BlockUtils.isBlockChorus(block)) {
            if (block.equals(Blocks.CHORUS_FLOWER)) {
                stacks.add(new ItemStack(Blocks.CHORUS_FLOWER));
            } else {
                stacks.addAll(BlockUtils.getBlockDrops(world, p));
            }
            world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
        }
    }

    public int getTopRowY() {
        int i = Integer.MIN_VALUE;
        for (BlockPos blockPos : chorus) {
            if (blockPos.getY() > i) i = blockPos.getY();
        }
        return i;
    }

    public List<BlockPos> getChorus() {
        return chorus;
    }
}
