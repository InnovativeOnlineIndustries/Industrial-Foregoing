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
