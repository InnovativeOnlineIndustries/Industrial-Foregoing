package com.buuz135.industrial.api.plant;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface IPlantRecollectable {

    public static final List<IPlantRecollectable> PLANT_RECOLLECTABLES = new ArrayList<>();

    /**
     * Checks if the Block can be harvested.
     *
     * @param world      The world where the Block is located.
     * @param pos        The position where the Block is located.
     * @param blockState The IBlockState of the Block.
     * @return True if it can be harvested, false if it can't be harvested.
     */
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState);

    /**
     * Harvests the block
     *
     * @param world      The world where the Block is located.
     * @param pos        The position where the Block is located.
     * @param blockState The IBlockState of the Block.
     * @return A list of the drops of that Block.
     */
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState);


    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState);
}
