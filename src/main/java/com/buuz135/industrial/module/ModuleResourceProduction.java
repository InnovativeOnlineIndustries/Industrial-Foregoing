package com.buuz135.industrial.module;

import com.buuz135.industrial.block.resourceproduction.ResourcefulFurnaceBlock;
import com.buuz135.industrial.block.resourceproduction.SludgeRefinerBlock;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModuleResourceProduction implements IModule {

    public static AdvancedTitaniumTab TAB_RESOURCE = new AdvancedTitaniumTab(Reference.MOD_ID + "_resource_production", true);

    public static ResourcefulFurnaceBlock RESOURCEFUL_FURNACE = new ResourcefulFurnaceBlock();
    public static SludgeRefinerBlock SLUDGE_REFINER = new SludgeRefinerBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("furnace").
                content(Block.class, RESOURCEFUL_FURNACE));
        features.add(Feature.builder("sludge_refiner").
                content(Block.class, SLUDGE_REFINER));
        return features;
    }

}
