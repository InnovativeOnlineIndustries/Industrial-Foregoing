/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.module;

import com.buuz135.industrial.block.misc.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleMisc implements IModule {

    public static AdvancedTitaniumTab TAB_MISC = new AdvancedTitaniumTab(Reference.MOD_ID + "_misc", true);
    public static StasisChamberBlock STASIS_CHAMBER = new StasisChamberBlock();
    public static MobDetectorBlock MOB_DETECTOR = new MobDetectorBlock();
    public static EnchantmentSorterBlock ENCHANTMENT_SORTER = new EnchantmentSorterBlock();
    public static EnchantmentApplicatorBlock ENCHANTMENT_APPLICATOR = new EnchantmentApplicatorBlock();
    public static EnchantmentExtractorBlock ENCHANTMENT_EXTRACTOR = new EnchantmentExtractorBlock();
    public static EnchantmentFactoryBlock ENCHANTMENT_FACTORY = new EnchantmentFactoryBlock();
    public static InfinityChargerBlock INFINITY_CHARGER = new InfinityChargerBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("stasis_chamber")
                .content(Block.class, STASIS_CHAMBER)
                .event(EventManager.forge(LivingEvent.LivingUpdateEvent.class).filter(livingUpdateEvent -> livingUpdateEvent.getEntityLiving() instanceof Mob && livingUpdateEvent.getEntityLiving().getPersistentData().contains("StasisChamberTime")).process(livingUpdateEvent -> {
                    long time = livingUpdateEvent.getEntityLiving().getPersistentData().getLong("StasisChamberTime");
                    if (time + 50 <= livingUpdateEvent.getEntityLiving().level.getGameTime()) {
                        ((Mob) livingUpdateEvent.getEntityLiving()).setNoAi(false);
                    }
                }))
        );
        features.add(createFeature(MOB_DETECTOR));
        features.add(createFeature(ENCHANTMENT_SORTER));
        features.add(createFeature(ENCHANTMENT_APPLICATOR));
        features.add(createFeature(ENCHANTMENT_EXTRACTOR));
        features.add(createFeature(ENCHANTMENT_FACTORY));
        features.add(createFeature(INFINITY_CHARGER));
        return features;
    }
}
