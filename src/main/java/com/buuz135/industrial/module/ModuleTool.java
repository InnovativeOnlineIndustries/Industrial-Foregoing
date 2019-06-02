package com.buuz135.industrial.module;

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.infinity.ItemInfinityDrill;
import com.hrznstudio.titanium.module.Feature;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModuleTool implements IModule {

    public static MeatFeederItem MEAT_FEEDER;
    public static MobImprisonmentToolItem MOB_IMPRISONMENT_TOOL;
    public static ItemInfinityDrill INFINITY_DRILL;

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("meat_feeder").content(Item.class, MEAT_FEEDER = new MeatFeederItem()));
        features.add(Feature.builder("mob_imprisonment_tool").content(Item.class, MOB_IMPRISONMENT_TOOL = new MobImprisonmentToolItem()));
        features.add(Feature.builder("infinity_drill").content(Item.class, INFINITY_DRILL = new ItemInfinityDrill()));
        return features;
    }
}
