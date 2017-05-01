package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.generator.PetrifiedFuelGeneratorTile;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PetrifiedFuelGeneratorBlock extends CustomOrientedBlock<PetrifiedFuelGeneratorTile> {

    public PetrifiedFuelGeneratorBlock() {
        super("petrified_fuel_generator", PetrifiedFuelGeneratorTile.class, Material.ROCK);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

}
