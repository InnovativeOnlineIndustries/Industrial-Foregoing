package com.buuz135.industrial.module;

import com.buuz135.industrial.block.generator.*;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleGenerator implements IModule {

    public static AdvancedTitaniumTab TAB_GENERATOR = new AdvancedTitaniumTab(Reference.MOD_ID + "_generator", true);

    public static PitifulGeneratorBlock PITIFUL_GENERATOR = new PitifulGeneratorBlock();
    public static BioReactorBlock BIOREACTOR = new BioReactorBlock();
    public static BiofuelGeneratorBlock BIOFUEL_GENERATOR = new BiofuelGeneratorBlock();
    public static List<MycelialGeneratorBlock> MYCELIAL_GENERATORS = new ArrayList<>();
    public static MycelialReactorBlock MYCELIAL_REACTOR = new MycelialReactorBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(createFeature(PITIFUL_GENERATOR));
        features.add(createFeature(BIOREACTOR));
        features.add(createFeature(BIOFUEL_GENERATOR));
        Feature.Builder mycelial = Feature.builder("mycelial_generators");
        for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
            MycelialGeneratorBlock block = new MycelialGeneratorBlock(type);
            MYCELIAL_GENERATORS.add(block);
            mycelial.content(Block.class, block);
        }
        features.add(mycelial);
        features.add(createFeature(MYCELIAL_REACTOR));
        TAB_GENERATOR.addIconStack(new ItemStack(PITIFUL_GENERATOR));
        return features;
    }
}
