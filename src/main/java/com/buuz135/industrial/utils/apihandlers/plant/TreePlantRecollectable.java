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
package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TreePlantRecollectable extends PlantRecollectable {

    private final HashMap<BlockPos, TreeCache> treeCache;

    public TreePlantRecollectable() {
        super("tree");
        this.treeCache = new HashMap<>();
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        if (treeCache.containsKey(pos)) return true;
        if (BlockUtils.isLog(world, pos)) {
            TreeCache cache = new TreeCache(world, pos);
            cache.scanForTreeBlockSection();
            treeCache.put(pos, cache);
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState) {
        return new ArrayList<>();
    }


    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState, Object... extras) {
        List<ItemStack> itemStacks = new ArrayList<>();
        if (treeCache.containsKey(pos)) {
            TreeCache cache = treeCache.get(pos);
            if (cache.getWoodCache().isEmpty() && cache.getLeavesCache().isEmpty() && canBeHarvested(world, pos, blockState)) {
                cache.scanForTreeBlockSection();
            }
            int operations = BlockRegistry.cropRecolectorBlock.getTreeOperations();
            if (BlockRegistry.cropRecolectorBlock.isReducedChunkUpdates()) {
                operations = 0;
                if ((cache.getLeavesCache().size() + cache.getWoodCache().size()) <= ((int) extras[1]) * BlockRegistry.cropRecolectorBlock.getTreeOperations()) {
                    operations = cache.getLeavesCache().size() + cache.getWoodCache().size();
                }
            }
            for (int i = 0; i < operations; ++i) {
                if (cache.getWoodCache().isEmpty() && cache.getLeavesCache().isEmpty()) break;
                if (!cache.getLeavesCache().isEmpty())
                    itemStacks.addAll(cache.chop(cache.getLeavesCache(), (Boolean) extras[0]));
                else itemStacks.addAll(cache.chop(cache.getWoodCache(), (Boolean) extras[0]));
            }
            if (cache.getWoodCache().isEmpty() && cache.getLeavesCache().isEmpty()) treeCache.remove(pos);
        }
        return itemStacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState) {
        return !canBeHarvested(world, pos, blockState);
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.any_tree", "text.industrialforegoing.plant.slime_tree");
    }

    @Override
    public Type getRecollectableType() {
        return Type.TREE;
    }

}
