package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeCache {

    private Queue<BlockPos> woodCache;
    private Queue<BlockPos> leavesCache;
    private World world;

    public TreeCache(World world, BlockPos current) {
        this.woodCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ())).reversed());
        this.leavesCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ())).reversed());
        this.world = world;
        Stack<BlockPos> tree = new Stack<>();
        tree.push(current);
        while (!tree.isEmpty()) {
            BlockPos checking = tree.pop();
            if (BlockUtils.isLog(world, checking) || BlockUtils.isLeaves(world, checking)) {
                Iterable<BlockPos> area = BlockPos.getAllInBox(checking.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH).offset(EnumFacing.WEST), checking.offset(EnumFacing.UP).offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
                for (BlockPos blockPos : area) {
                    if (BlockUtils.isLog(world, blockPos) && !woodCache.contains(blockPos) && blockPos.distanceSq(current.getX(), current.getY(), current.getZ()) <= 1000) {
                        tree.push(blockPos);
                        woodCache.add(blockPos);
                    } else if (BlockUtils.isLeaves(world, blockPos) && !leavesCache.contains(blockPos) && blockPos.distanceSq(current.getX(), current.getY(), current.getZ()) <= 1000) {
                        tree.push(blockPos);
                        leavesCache.add(blockPos);
                    }
                }
            }
        }
    }


    public List<ItemStack> chop(Queue<BlockPos> cache) {
        BlockPos p = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (BlockUtils.isLeaves(world, p) || BlockUtils.isLog(world, p)) {
            IBlockState s = world.getBlockState(p);
            s.getBlock().getDrops(stacks, world, p, s, 0);
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
}
