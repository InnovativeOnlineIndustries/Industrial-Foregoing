package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class BlockNetherWartRecollectable extends PlantRecollectable {

    public BlockNetherWartRecollectable() {
        super("blocknetherwart");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        return blockState.getBlock() instanceof BlockNetherWart && blockState.getValue(BlockNetherWart.AGE) >= 3;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(BlockUtils.getBlockDrops(world, pos));
        world.setBlockToAir(pos);
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState) {
        return true;
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.nether_wart");
    }
}
