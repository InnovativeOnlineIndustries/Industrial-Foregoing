package com.buuz135.industrial.module;

import com.buuz135.industrial.block.resourceproduction.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleResourceProduction implements IModule {

    public static AdvancedTitaniumTab TAB_RESOURCE = new AdvancedTitaniumTab(Reference.MOD_ID + "_resource_production", true);

    public static ResourcefulFurnaceBlock RESOURCEFUL_FURNACE = new ResourcefulFurnaceBlock();
    public static SludgeRefinerBlock SLUDGE_REFINER = new SludgeRefinerBlock();
    public static WaterCondensatorBlock WATER_CONDENSATOR = new WaterCondensatorBlock();
    public static MechanicalDirtBlock MECHANICAL_DIRT = new MechanicalDirtBlock();
    public static BlockPlacerBlock BLOCK_PLACER = new BlockPlacerBlock();
    public static BlockBreakerBlock BLOCK_BREAKER = new BlockBreakerBlock();
    public static FluidCollectorBlock FLUID_COLLECTOR = new FluidCollectorBlock();
    public static FluidPlacerBlock FLUID_PLACER = new FluidPlacerBlock();
    public static DyeMixerBlock DYE_MIXER = new DyeMixerBlock();
    public static SporesRecreatorBlock SPORES_RECREATOR = new SporesRecreatorBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(createFeature(RESOURCEFUL_FURNACE));
        features.add(createFeature(SLUDGE_REFINER));
        features.add(createFeature(WATER_CONDENSATOR));
        features.add(createFeature(MECHANICAL_DIRT));
        features.add(createFeature(BLOCK_PLACER));
        features.add(createFeature(BLOCK_BREAKER));
        features.add(createFeature(FLUID_COLLECTOR));
        features.add(createFeature(FLUID_PLACER));
        features.add(createFeature(DYE_MIXER));
        features.add(createFeature(SPORES_RECREATOR));
        TAB_RESOURCE.addIconStack(new ItemStack(WATER_CONDENSATOR));
        return features;
    }

}
