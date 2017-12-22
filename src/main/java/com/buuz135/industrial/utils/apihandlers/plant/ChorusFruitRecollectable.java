package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChorusFruitRecollectable extends PlantRecollectable {

    private final HashMap<BlockPos, ChorusCache> chorusCacheHashMap;

    public ChorusFruitRecollectable() {
        super("chorus_fruit");
        chorusCacheHashMap = new HashMap<>();
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        if (chorusCacheHashMap.containsKey(pos)) return true;
        if (BlockUtils.isChorus(world, pos)) {
            ChorusCache chorusCache = new ChorusCache(world, pos);
            if (chorusCache.isFullyGrown()) {
                chorusCacheHashMap.put(pos, chorusCache);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState) {
        List<ItemStack> stacks = new ArrayList<>();
        if (chorusCacheHashMap.containsKey(pos)) {
            ChorusCache chorusCache = chorusCacheHashMap.get(pos);
            stacks.addAll(chorusCache.chop());
            if (chorusCache.getChorus().isEmpty()) chorusCacheHashMap.remove(pos);
        }
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState) {
        return !canBeHarvested(world, pos, blockState);
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList(" - Chorus plant");
    }
}
