//package com.buuz135.industrial.plugin;
//
//import com.buuz135.industrial.item.infinity.item.ItemInfinityDrill;
//import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
//import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
//import com.hrznstudio.titanium.plugin.PluginPhase;
//import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
//import org.apache.commons.lang3.ArrayUtils;
//
//@FeaturePlugin(value = "astralsorcery", type = FeaturePlugin.FeaturePluginType.MOD)
//public class AstralSorceryPlugin implements FeaturePluginInstance {
//    @Override
//    public void execute(PluginPhase phase) {
//        if (phase == PluginPhase.COMMON_SETUP) {
//            ItemInfinityDrill.mineableMaterials = ArrayUtils.addAll(ItemInfinityDrill.mineableMaterials, MaterialsAS.MARBLE, MaterialsAS.BLACK_MARBLE);
//        }
//    }
//}
