/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class HydratorTile extends WorkingAreaElectricMachine {

    public HydratorTile() {
        super(HydratorTile.class.getName().hashCode());
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea());
        boolean hasWorked = false;
        for (BlockPos pos : blockPosList) {
            if (world.getBlockState(pos).getBlock() instanceof IGrowable) {
                world.getBlockState(pos).getBlock().randomTick(world, pos, world.getBlockState(pos), world.rand);
                hasWorked = true;
            }
        }
        return hasWorked ? 1 : 0;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return super.getWorkingArea().offset(0, -1, 0);
    }
}
