package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SweetBerriesPlantRecollectable extends PlantRecollectable {

    public SweetBerriesPlantRecollectable() {
        super("sweetberries");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, BlockState blockState) {
        return blockState.has(SweetBerryBushBlock.AGE) && blockState.get(SweetBerryBushBlock.AGE) == 3;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, BlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(BlockUtils.getBlockDrops(world, pos));
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, BlockState blockState) {
        return true;
    }
}
