/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.api.plant;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class PlantRecollectable extends IForgeRegistryEntry.Impl<PlantRecollectable> {

    public PlantRecollectable(String name) {
        setRegistryName(name);
    }

    /**
     * Checks if the Block can be harvested.
     *
     * @param world      The world where the Block is located.
     * @param pos        The position where the Block is located.
     * @param blockState The IBlockState of the Block.
     * @return True if it can be harvested, false if it can't be harvested.
     */
    public abstract boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState);

    /**
     * Harvests the block
     *
     * @param world      The world where the Block is located.
     * @param pos        The position where the Block is located.
     * @param blockState The IBlockState of the Block.
     * @return A list of the drops of that Block.
     */
    public abstract List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState);

    /**
     * Harvests the block
     *
     * @param world      The world where the Block is located.
     * @param pos        The position where the Block is located.
     * @param blockState The IBlockState of the Block.
     * @param extras     An extra of values inserted in the Gatherer.
     * @return A list of the drops of that Block.
     */
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState, Object... extras) {
        return doHarvestOperation(world, pos, blockState);
    }

    /**
     * Checks if the next block should be checked from the harvester.
     *
     * @param world      The world where the Block is located.
     * @param pos        The position where the Block is located.
     * @param blockState The IBlockState of the Block.
     * @return True if the harvester should check the next position or false if it should keep checking the current position.
     */
    public abstract boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState);

    /**
     * Returns the priority of the handler. The bigger it is the more priority it has.
     *
     * @return The priority.
     */
    public int getPriority() {
        return -1;
    }

    /**
     * Returns a list of names of the blocks that can harvest.
     *
     * @return The list
     */
    public List<String> getRecollectablesNames() {
        return new ArrayList<>();
    }

    /**
     * Returns what type of plant recollects
     *
     * @return The type of plant
     */
    public Type getRecollectableType() {
        return Type.PLANT;
    }

    public enum Type {
        TREE,
        PLANT,
        ANY
    }
}
