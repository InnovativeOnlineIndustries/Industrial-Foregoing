package com.buuz135.industrial.module;

import com.buuz135.industrial.block.resourceproduction.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;

import java.util.ArrayList;
import java.util.List;

public class ModuleResourceProduction implements IModule {

    public static AdvancedTitaniumTab TAB_RESOURCE = new AdvancedTitaniumTab(Reference.MOD_ID + "_resource_production", true);

    public static ResourcefulFurnaceBlock RESOURCEFUL_FURNACE = new ResourcefulFurnaceBlock();
    public static SludgeRefinerBlock SLUDGE_REFINER = new SludgeRefinerBlock();
    public static WaterCondensatorBlock WATER_CONDENSATOR = new WaterCondensatorBlock();
    public static MechanicalDirtBlock MECHANICAL_DIRT = new MechanicalDirtBlock();
    public static BlockPlacerBlock BLOCK_PLACER = new BlockPlacerBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(createFeature(RESOURCEFUL_FURNACE));
        features.add(createFeature(SLUDGE_REFINER));
        features.add(createFeature(WATER_CONDENSATOR));
        features.add(createFeature(MECHANICAL_DIRT));
        features.add(createFeature(BLOCK_PLACER));
        return features;
    }

}
