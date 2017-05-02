package com.buuz135.industrial.utils;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    public static List<BlockPos> getBlockPosInAABB(AxisAlignedBB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; ++x) {
            for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; ++z) {
                for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; ++y) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }
}
