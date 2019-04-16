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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;

import java.util.List;

public class HydratorTile extends WorkingAreaElectricMachine {

    private AABBTicket farmlandTicket;

    public HydratorTile() {
        super(HydratorTile.class.getName().hashCode());
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            farmlandTicket = FarmlandWaterManager.addAABBTicket(world, getWateringArea());
            updateTicket();
        }
    }

    public void updateTicket() {
        if (!world.isRemote) {
            farmlandTicket.validate();
        }
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (farmlandTicket == null || !farmlandTicket.axisAlignedBB.equals(getWateringArea())) {
            if (farmlandTicket != null) farmlandTicket.invalidate();
            farmlandTicket = FarmlandWaterManager.addAABBTicket(world, getWateringArea());
            updateTicket();
        }
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

    @Override
    public void onChunkUnload() {
        if (!world.isRemote) {
            farmlandTicket.invalidate();
        }
    }

    public AxisAlignedBB getWateringArea() {
        return getWorkingArea().offset(0, -1, 0).grow(0, 1, 0);
    }

    public AABBTicket getFarmlandTicket() {
        return farmlandTicket;
    }
}
