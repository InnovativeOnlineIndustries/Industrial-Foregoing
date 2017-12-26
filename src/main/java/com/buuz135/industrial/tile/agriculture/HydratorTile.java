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
