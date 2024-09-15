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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.IndustrialBlockItem;
import com.buuz135.industrial.block.misc.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


public class ModuleMisc implements IModule {

    public static TitaniumTab TAB_MISC = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "misc"));
    public static BlockWithTile STASIS_CHAMBER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("stasis_chamber", () -> new StasisChamberBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);
    public static BlockWithTile MOB_DETECTOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("mob_detector", () -> new MobDetectorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);
    public static BlockWithTile ENCHANTMENT_SORTER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("enchantment_sorter", () -> new EnchantmentSorterBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);
    public static BlockWithTile ENCHANTMENT_APPLICATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("enchantment_applicator", () -> new EnchantmentApplicatorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);
    public static BlockWithTile ENCHANTMENT_EXTRACTOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("enchantment_extractor", () -> new EnchantmentExtractorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);
    public static BlockWithTile ENCHANTMENT_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("enchantment_factory", () -> new EnchantmentFactoryBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);
    public static BlockWithTile INFINITY_CHARGER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("infinity_charger", () -> new InfinityChargerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_MISC), TAB_MISC);

    @Override
    public void generateFeatures(DeferredRegistryHelper helper) {
        EventManager.forge(EntityTickEvent.Pre.class).filter(livingUpdateEvent -> livingUpdateEvent.getEntity() instanceof Mob && livingUpdateEvent.getEntity().getPersistentData().contains("StasisChamberTime")).process(livingUpdateEvent -> {
            long time = livingUpdateEvent.getEntity().getPersistentData().getLong("StasisChamberTime");
            if (time + 50 <= livingUpdateEvent.getEntity().level().getGameTime()) {
                ((Mob) livingUpdateEvent.getEntity()).setNoAi(false);
            }
        }).subscribe();
    }
}
