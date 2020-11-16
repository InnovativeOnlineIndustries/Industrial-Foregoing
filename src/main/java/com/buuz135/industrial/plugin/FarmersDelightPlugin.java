package com.buuz135.industrial.plugin;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
import com.hrznstudio.titanium.plugin.PluginPhase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import vectorwing.farmersdelight.blocks.RiceCropBlock;
import vectorwing.farmersdelight.blocks.TomatoesBlock;

import java.util.ArrayList;
import java.util.List;

@FeaturePlugin(value = "farmersdelight", type = FeaturePlugin.FeaturePluginType.MOD)
public class FarmersDelightPlugin implements FeaturePluginInstance {

    @Override
    public void execute(PluginPhase phase) {
        if (phase == PluginPhase.CONSTRUCTION) {
            EventManager.modGeneric(RegistryEvent.Register.class, PlantRecollectable.class).process(register -> {
                ((RegistryEvent.Register) register).getRegistry().registerAll(new TomatoesPlantRecollectable(), new RiceRecollectable());
            }).subscribe();
        }
    }

    private class RiceRecollectable extends PlantRecollectable {

        public RiceRecollectable() {
            super("fd_rice");
        }

        @Override
        public boolean canBeHarvested(World world, BlockPos pos, BlockState blockState) {
            return blockState.getBlock() instanceof RiceCropBlock && ((RiceCropBlock) blockState.getBlock()).isMaxAge(blockState);
        }

        @Override
        public List<ItemStack> doHarvestOperation(World world, BlockPos pos, BlockState blockState) {
            List<ItemStack> itemStacks = new ArrayList<>(BlockUtils.getBlockDrops(world, pos));
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            return itemStacks;
        }

        @Override
        public boolean shouldCheckNextPlant(World world, BlockPos pos, BlockState blockState) {
            return true;
        }
    }

    public class TomatoesPlantRecollectable extends PlantRecollectable {

        public TomatoesPlantRecollectable() {
            super("fd_tomatoes");
        }

        @Override
        public boolean canBeHarvested(World world, BlockPos pos, BlockState blockState) {
            return blockState.getBlock() instanceof TomatoesBlock && ((TomatoesBlock) blockState.getBlock()).isMaxAge(blockState);
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

}
