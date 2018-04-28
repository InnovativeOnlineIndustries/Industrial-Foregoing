package com.buuz135.industrial.utils;

import com.buuz135.industrial.tile.block.CustomAreaOrientedBlock;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorkUtils {

    public static boolean isDisabled(Block block) {
        return block instanceof CustomOrientedBlock && ((CustomOrientedBlock) block).isWorkDisabled();
    }

    public static CustomAreaOrientedBlock getBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof CustomAreaOrientedBlock ? (CustomAreaOrientedBlock) world.getBlockState(pos).getBlock() : null;
    }

    public static int getMachineWidth(World world, BlockPos pos) {
        return getBlock(world, pos) != null ? getBlock(world, pos).getMaxWidth() : 0;
    }

    public static int getMachineHeight(World world, BlockPos pos) {
        return getBlock(world, pos) != null ? getBlock(world, pos).getHeight() : 0;
    }

    public static AxisAlignedBB getMachineWorkingArea(World world, BlockPos pos, int width, int height, EnumFacing facing) {
        return getBlock(world, pos) != null ? getBlock(world, pos).getType().getArea(pos, width, height, facing) : generateBlockSizeBox(pos);
    }

    public static AxisAlignedBB generateBlockSizeBox(BlockPos pos) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public static boolean acceptsRangeAddons(World world, BlockPos pos) {
        return getBlock(world, pos) != null && getBlock(world, pos).isAcceptsRangeAddon();
    }
}
