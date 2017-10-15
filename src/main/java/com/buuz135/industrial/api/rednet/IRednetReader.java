package com.buuz135.industrial.api.rednet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class IRednetReader extends IForgeRegistryEntry.Impl<IRednetReader> {

    public abstract int getRedstoneValue(World world, BlockPos pos, IBlockState blockState, EnumFacing facing);

    public abstract boolean isBlockValid(World world, BlockPos pos, IBlockState blockState, EnumFacing facing);

}
