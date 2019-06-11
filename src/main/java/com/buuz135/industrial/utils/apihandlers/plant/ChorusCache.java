/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChorusCache {

    private List<BlockPos> chorus;
    private World world;

    public ChorusCache(World world, BlockPos current) {
        this.world = world;
        this.chorus = new ArrayList<>();
        Stack<BlockPos> chorus = new Stack<>();
        chorus.push(current);
        while (!chorus.isEmpty()) {
            BlockPos checking = chorus.pop();
            if (BlockUtils.isChorus(world, checking)) {
                Iterable<BlockPos> area = BlockPos.getAllInBoxMutable(checking.offset(Direction.DOWN).offset(Direction.SOUTH).offset(Direction.WEST), checking.offset(Direction.UP).offset(Direction.NORTH).offset(Direction.EAST));
                for (BlockPos blockPos : area) {
                    if (BlockUtils.isChorus(world, blockPos) && !this.chorus.contains(blockPos) && blockPos.distanceSq(current.getX(), current.getY(), current.getZ(), true) <= 1000) {
                        chorus.push(blockPos);
                        this.chorus.add(blockPos);
                    }
                }
            }
        }
    }

    public boolean isFullyGrown() {
        return chorus.stream().map(blockpos -> world.getBlockState(blockpos)).allMatch(blockState -> blockState.getBlock().equals(Blocks.CHORUS_PLANT) || (blockState.getBlock().equals(Blocks.CHORUS_FLOWER) && blockState.get(ChorusFlowerBlock.AGE) == 5));
    }

    public List<ItemStack> chop() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        int maxY = getTopRowY();
        chorus.stream().filter(pos -> pos.getY() == maxY).forEach(pos -> chop(stacks, pos));
        chorus.removeIf(pos -> pos.getY() == maxY);
        return stacks;
    }

    public void chop(NonNullList<ItemStack> stacks, BlockPos p) {
        if (BlockUtils.isChorus(world, p)) {
            if (world.getBlockState(p).getBlock().equals(Blocks.CHORUS_FLOWER)) {
                stacks.add(new ItemStack(Blocks.CHORUS_FLOWER));
            } else {
                stacks.addAll(BlockUtils.getBlockDrops(world, p));
            }
            world.setBlockState(p, Blocks.AIR.getDefaultState());
        }
    }

    public int getTopRowY() {
        int i = 0;
        for (BlockPos blockPos : chorus) {
            if (blockPos.getY() > i) i = blockPos.getY();
        }
        return i;
    }

    public List<BlockPos> getChorus() {
        return chorus;
    }
}
