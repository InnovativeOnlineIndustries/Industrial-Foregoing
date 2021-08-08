/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

package com.buuz135.industrial.plugin;

import blusunrize.immersiveengineering.common.blocks.plant.EnumHempGrowth;
import blusunrize.immersiveengineering.common.blocks.plant.HempBlock;
import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
import com.hrznstudio.titanium.plugin.PluginPhase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;

@FeaturePlugin(value = "immersiveengineering", type = FeaturePlugin.FeaturePluginType.MOD)
public class ImmersiveEngineeringPlugin implements FeaturePluginInstance {

    @Override
    public void execute(PluginPhase phase) {
        if (phase == PluginPhase.CONSTRUCTION) {
            EventManager.modGeneric(RegistryEvent.Register.class, PlantRecollectable.class).process(register -> {
                ((RegistryEvent.Register) register).getRegistry().register(new HempPlantRecollectable());
            }).subscribe();
        }
    }

    private class HempPlantRecollectable extends PlantRecollectable {

        public HempPlantRecollectable() {
            super("ie_hemp");
        }

        @Override
        public boolean canBeHarvested(World world, BlockPos pos, BlockState blockState) {
            return blockState.getBlock() instanceof HempBlock && world.getBlockState(pos.up()).getBlock() instanceof HempBlock && world.getBlockState(pos.up()).get(HempBlock.GROWTH) == EnumHempGrowth.TOP0;
        }

        @Override
        public List<ItemStack> doHarvestOperation(World world, BlockPos pos, BlockState blockState) {
            List<ItemStack> itemStacks = new ArrayList<>(BlockUtils.getBlockDrops(world, pos.up()));
            world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
            return itemStacks;
        }

        @Override
        public boolean shouldCheckNextPlant(World world, BlockPos pos, BlockState blockState) {
            return true;
        }
    }

}
