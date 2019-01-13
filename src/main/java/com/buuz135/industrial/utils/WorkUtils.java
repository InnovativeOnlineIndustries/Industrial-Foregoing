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
