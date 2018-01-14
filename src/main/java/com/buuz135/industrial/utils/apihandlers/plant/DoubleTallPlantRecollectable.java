package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class DoubleTallPlantRecollectable extends PlantRecollectable {

    public DoubleTallPlantRecollectable() {
        super("blocksugarandcactus");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        return blockState.getBlock() instanceof BlockCactus || blockState.getBlock() instanceof BlockReed;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        harvestBlock(stacks, world, pos.offset(EnumFacing.UP, 2));
        harvestBlock(stacks, world, pos.offset(EnumFacing.UP, 1));
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState) {
        return true;
    }

    private void harvestBlock(NonNullList<ItemStack> stacks, World world, BlockPos pos) {
        if (canBeHarvested(world, pos, world.getBlockState(pos))) {
            world.getBlockState(pos).getBlock().getDrops(stacks, world, pos, world.getBlockState(pos), 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.sugar_cane", "text.industrialforegoing.plant.cactus");
    }
}
