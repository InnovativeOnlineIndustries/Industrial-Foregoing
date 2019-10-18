package com.buuz135.industrial.module;

import com.buuz135.industrial.block.generator.BioReactorBlock;
import com.buuz135.industrial.block.generator.PitifulGeneratorBlock;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleGenerator implements IModule {

    public static AdvancedTitaniumTab TAB_GENERATOR = new AdvancedTitaniumTab(Reference.MOD_ID + "_generator", true);

    public static PitifulGeneratorBlock PITIFUL_GENERATOR = new PitifulGeneratorBlock();
    public static BioReactorBlock BIOREACTOR = new BioReactorBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(createFeature(PITIFUL_GENERATOR));
        features.add(createFeature(BIOREACTOR));
        TAB_GENERATOR.addIconStack(new ItemStack(PITIFUL_GENERATOR));
        return features;
    }
}
