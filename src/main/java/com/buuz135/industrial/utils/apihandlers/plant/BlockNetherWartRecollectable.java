package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.IPlantRecollectable;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BlockNetherWartRecollectable implements IPlantRecollectable {

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        return blockState.getBlock() instanceof BlockNetherWart && blockState.getValue(BlockNetherWart.AGE) >= 3;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        blockState.getBlock().getDrops(stacks, world, pos, blockState, 0);
        world.setBlockToAir(pos);
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState) {
        return true;
    }
}
