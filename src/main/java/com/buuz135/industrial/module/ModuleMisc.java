package com.buuz135.industrial.module;

import com.buuz135.industrial.block.misc.EnchantmentSorterBlock;
import com.buuz135.industrial.block.misc.MobDetectorBlock;
import com.buuz135.industrial.block.misc.StasisChamberBlock;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleMisc implements IModule {

    public static AdvancedTitaniumTab TAB_MISC = new AdvancedTitaniumTab(Reference.MOD_ID + "_misc", true);
    public static StasisChamberBlock STASIS_CHAMBER = new StasisChamberBlock();
    public static MobDetectorBlock MOB_DETECTOR = new MobDetectorBlock();
    public static EnchantmentSorterBlock ENCHANTMENT_SORTER = new EnchantmentSorterBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("stasis_chamber")
                .content(Block.class, STASIS_CHAMBER)
                .event(EventManager.forge(LivingEvent.LivingUpdateEvent.class).filter(livingUpdateEvent -> livingUpdateEvent.getEntityLiving() instanceof MobEntity && livingUpdateEvent.getEntityLiving().getPersistentData().contains("StasisChamberTime")).process(livingUpdateEvent -> {
                    long time = livingUpdateEvent.getEntityLiving().getPersistentData().getLong("StasisChamberTime");
                    if (time + 40 <= livingUpdateEvent.getEntityLiving().world.getGameTime()) {
                        ((MobEntity) livingUpdateEvent.getEntityLiving()).setNoAI(false);
                    }
                }))
        );
        features.add(createFeature(MOB_DETECTOR));
        features.add(createFeature(ENCHANTMENT_SORTER));
        return features;
    }
}
