package com.buuz135.industrial.module;

import com.buuz135.industrial.block.resourceproduction.ResourcefulFurnaceBlock;
import com.buuz135.industrial.block.resourceproduction.SludgeRefinerBlock;
import com.buuz135.industrial.block.resourceproduction.WaterCondensatorBlock;
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

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(createFeature(RESOURCEFUL_FURNACE));
        features.add(createFeature(SLUDGE_REFINER));
        features.add(createFeature(WATER_CONDENSATOR));
        return features;
    }

}
