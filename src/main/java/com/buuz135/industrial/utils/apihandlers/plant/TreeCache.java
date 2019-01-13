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

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.*;

public class TreeCache {

    private Queue<BlockPos> woodCache;
    private Queue<BlockPos> leavesCache;
    private World world;
    private BlockPos current;

    public TreeCache(World world, BlockPos current) {
        this.woodCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ())).reversed());
        this.leavesCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(current.getX(), ((BlockPos) value).getY(), current.getZ())).reversed());
        this.world = world;
        this.current = current;
    }

    public List<ItemStack> chop(Queue<BlockPos> cache, boolean shear) {
        BlockPos p = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (BlockUtils.isLeaves(world, p) || BlockUtils.isLog(world, p)) {
            IBlockState s = world.getBlockState(p);
            if (s.getBlock() instanceof IShearable && shear) {
                stacks.addAll(((IShearable) s.getBlock()).onSheared(new ItemStack(Items.SHEARS), world, p, 0));
            } else {
                stacks.addAll(BlockUtils.getBlockDrops(world, p));
            }
            world.setBlockToAir(p);
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
        for (BlockPos pos : BlockPos.getAllInBox(yCheck.add(1, 0, 0), yCheck.add(0, 0, 1))) {
            BlockPos tempHigh = getHighestBlock(pos);
            if (tempHigh.getY() > yCheck.getY()) yCheck = tempHigh;
        }
        yCheck = yCheck.add(0, -Math.min(20, yCheck.getY() - current.getY()), 0);
        Set<BlockPos> checkedPositions = new HashSet<>();
        Stack<BlockPos> tree = new Stack<>();
        BlockPos test = new BlockPos(current.getX(), yCheck.getY(), current.getZ());
        for (BlockPos pos : BlockPos.getAllInBox(test.add(1, 0, 0), test.add(0, 0, 1))) {
            tree.push(pos);
        }
        while (!tree.isEmpty()) {
            BlockPos checking = tree.pop();
            if (BlockUtils.isLeaves(world, checking) || BlockUtils.isLog(world, checking)) {
                for (BlockPos blockPos : BlockPos.getAllInBox(checking.add(-1, 0, -1), checking.add(1, 1, 1))) {
                    if (world.isAirBlock(blockPos) || checkedPositions.contains(blockPos) || blockPos.getDistance(current.getX(), current.getY(), current.getZ()) > BlockRegistry.cropRecolectorBlock.getMaxDistanceTreeBlocksScan())
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
        while (!this.world.isAirBlock(position.up()) && (BlockUtils.isLog(this.world, position.up()) || BlockUtils.isLeaves(this.world, position.up())))
            position = position.up();
        return position;
    }
}
