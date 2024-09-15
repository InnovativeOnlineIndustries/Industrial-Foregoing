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
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IShearable;

import java.util.*;

public class TreeCache {

    private Queue<BlockPos> woodCache;
    private Queue<BlockPos> leavesCache;
    private Level world;
    private BlockPos current;

    public TreeCache(Level world, BlockPos current) {
        this.woodCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distSqr(new Vec3i(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ()))).reversed());
        this.leavesCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distSqr(new Vec3i(current.getX(), ((BlockPos) value).getY(), current.getZ()))).reversed());
        this.world = world;
        this.current = current;
    }

    public List<ItemStack> chop(Queue<BlockPos> cache, boolean shear) {
        BlockPos p = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (BlockUtils.isLeaves(world, p) || BlockUtils.isLog(world, p)) {
            BlockState s = world.getBlockState(p);
            if (s.getBlock() instanceof IShearable shearable && shear) {
                stacks.addAll(shearable.onSheared(null, new ItemStack(Items.SHEARS), world, p));
            } else {
                stacks.addAll(BlockUtils.getBlockDrops(world, p));
            }
            world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
        }
        cache.poll();
        return stacks;
    }

    public Queue<BlockPos> getWoodCache() {
        return woodCache;
    }

    public Queue<BlockPos> getLeavesCache() {
        return leavesCache;
    }

    public void scanForTreeBlockSection() {
        BlockPos yCheck = getHighestBlock(current);
        for (BlockPos pos : BlockPos.betweenClosed(yCheck.offset(1, 0, 0), yCheck.offset(0, 0, 1))) {
            BlockPos tempHigh = getHighestBlock(pos.immutable());
            if (tempHigh.getY() > yCheck.getY()) yCheck = tempHigh;
        }
        yCheck = yCheck.offset(0, -Math.min(20, yCheck.getY() - current.getY()), 0);
        Set<BlockPos> checkedPositions = new HashSet<>();
        Stack<BlockPos> tree = new Stack<>();
        BlockPos test = new BlockPos(current.getX(), yCheck.getY(), current.getZ());
        for (BlockPos pos : BlockPos.betweenClosed(test.offset(1, 0, 0), test.offset(0, 0, 1))) {
            tree.push(pos.immutable());
        }
        while (!tree.isEmpty()) {
            BlockPos checking = tree.pop();
            if (BlockUtils.isLeaves(world, checking) || BlockUtils.isLog(world, checking)) {
                for (BlockPos pos : BlockPos.betweenClosed(checking.offset(-1, 0, -1), checking.offset(1, 1, 1))) {
                    BlockPos blockPos = pos.immutable();
                    if (world.isEmptyBlock(blockPos) || checkedPositions.contains(blockPos) || blockPos.distManhattan(new Vec3i(current.getX(), current.getY(), current.getZ())) > 100 /*BlockRegistry.cropRecolectorBlock.getMaxDistanceTreeBlocksScan()*/)
                        continue;
                    if (BlockUtils.isLeaves(world, blockPos)) {
                        tree.push(blockPos);
                        leavesCache.add(blockPos);
                        checkedPositions.add(blockPos);
                    } else if (BlockUtils.isLog(world, blockPos)) {
                        tree.push(blockPos);
                        woodCache.add(blockPos);
                        checkedPositions.add(blockPos);
                    }
                }
            }
        }
    }

    public BlockPos getHighestBlock(BlockPos position) {
        while (!this.world.isEmptyBlock(position.above()) && (BlockUtils.isLog(this.world, position.above()) || BlockUtils.isLeaves(this.world, position.above())))
            position = position.above();
        return position;
    }
}
