package com.buuz135.industrial.module;

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.infinity.ItemInfinityDrill;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleTool implements IModule {

    public static AdvancedTitaniumTab TAB_TOOL = new AdvancedTitaniumTab(Reference.MOD_ID + "_tool", true);

    public static MeatFeederItem MEAT_FEEDER;
    public static MobImprisonmentToolItem MOB_IMPRISONMENT_TOOL;
    public static ItemInfinityDrill INFINITY_DRILL;

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("meat_feeder").content(Item.class, MEAT_FEEDER = new MeatFeederItem(TAB_TOOL)));
        features.add(Feature.builder("mob_imprisonment_tool").content(Item.class, MOB_IMPRISONMENT_TOOL = new MobImprisonmentToolItem(TAB_TOOL)));
        features.add(Feature.builder("infinity_drill").content(Item.class, INFINITY_DRILL = new ItemInfinityDrill(TAB_TOOL)));
        TAB_TOOL.addIconStack(new ItemStack(INFINITY_DRILL));
        return features;
    }
}
