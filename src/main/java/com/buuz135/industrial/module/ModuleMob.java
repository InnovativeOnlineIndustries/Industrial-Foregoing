package com.buuz135.industrial.module;

import com.buuz135.industrial.block.mob.MobDetectorBlock;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModuleMob implements IModule {

    public static AdvancedTitaniumTab TAB_MOB = new AdvancedTitaniumTab(Reference.MOD_ID + "_mob", true);
    public static MobDetectorBlock MOB_DETECTOR = new MobDetectorBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("mob_detector")
            .content(Block.class, MOB_DETECTOR)
        );
        return features;
    }

}
