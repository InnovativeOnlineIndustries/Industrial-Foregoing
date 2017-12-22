package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ChorusCache {

    private Queue<BlockPos> chorus;
    private World world;

    public ChorusCache(World world, BlockPos current) {
        this.world = world;
        chorus = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ())).reversed());
        Stack<BlockPos> chorus = new Stack<>();
        chorus.push(current);
        while (!chorus.isEmpty()) {
            BlockPos checking = chorus.pop();
            if (BlockUtils.isChorus(world, checking)) {
                Iterable<BlockPos> area = BlockPos.getAllInBox(checking.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH).offset(EnumFacing.WEST), checking.offset(EnumFacing.UP).offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
                for (BlockPos blockPos : area) {
                    if (BlockUtils.isChorus(world, blockPos) && !this.chorus.contains(blockPos) && blockPos.distanceSq(current.getX(), current.getY(), current.getZ()) <= 1000) {
                        chorus.push(blockPos);
                        this.chorus.add(blockPos);
                    }
                }
            }
        }
    }

    public boolean isFullyGrown() {
        return chorus.stream().map(blockpos -> world.getBlockState(blockpos)).allMatch(blockState -> blockState.getBlock().equals(Blocks.CHORUS_PLANT) || (blockState.getBlock().equals(Blocks.CHORUS_FLOWER) && Blocks.CHORUS_FLOWER.getMetaFromState(blockState) == 5));
    }

    public List<ItemStack> chop() {
        BlockPos p = chorus.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (BlockUtils.isChorus(world, p)) {
            if (world.getBlockState(p).getBlock().equals(Blocks.CHORUS_FLOWER))
                stacks.add(new ItemStack(Blocks.CHORUS_FLOWER));
            world.getBlockState(p).getBlock().getDrops(stacks, world, p, world.getBlockState(p), 0);
            world.setBlockToAir(p);
        }
        chorus.poll();
        return stacks;
    }

    public Queue<BlockPos> getChorus() {
        return chorus;
    }
}
