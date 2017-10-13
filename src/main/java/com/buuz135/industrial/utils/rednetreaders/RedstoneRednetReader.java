package com.buuz135.industrial.utils.rednetreaders;

import com.buuz135.industrial.api.rednet.IRednetReader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneRednetReader extends IRednetReader {

    public RedstoneRednetReader() {
        setRegistryName("redstone");
    }

    @Override
    public int getRedstoneValue(World world, BlockPos pos, IBlockState blockState, EnumFacing facing) {
        return blockState.getWeakPower(world, pos, facing);
    }

    @Override
    public boolean isBlockValid(World world, BlockPos pos, IBlockState blockState, EnumFacing facing) {
        return blockState.canProvidePower();
    }
}
