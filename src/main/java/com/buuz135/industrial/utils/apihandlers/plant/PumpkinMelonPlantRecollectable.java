package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class PumpkinMelonPlantRecollectable extends PlantRecollectable {

    public PumpkinMelonPlantRecollectable() {
        super("blockpumpkingandmelon");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        return blockState.getBlock() instanceof BlockPumpkin || blockState.getBlock() instanceof BlockMelon;
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
        return Arrays.asList("text.industrialforegoing.plant.pumpkin", "text.industrialforegoing.plant.melon");
    }
}
