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

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.agriculturehusbandry.*;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.apihandlers.plant.*;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleAgricultureHusbandry implements IModule {

    public static AdvancedTitaniumTab TAB_AG_HUS = new AdvancedTitaniumTab(Reference.MOD_ID + "_ag_hus", true);

    public static PlantGathererBlock PLANT_GATHERER = new PlantGathererBlock();
    public static SewerBlock SEWER = new SewerBlock();
    public static SewageComposterBlock SEWAGE_COMPOSTER = new SewageComposterBlock();
    public static PlantFertilizerBlock PLANT_FERTILIZER = new PlantFertilizerBlock();
    public static PlantSowerBlock PLANT_SOWER = new PlantSowerBlock();
    public static SlaughterFactoryBlock SLAUGHTER_FACTORY = new SlaughterFactoryBlock();
    public static AnimalRancherBlock ANIMAL_RANCHER = new AnimalRancherBlock();
    public static AnimalFeederBlock ANIMAL_FEEDER = new AnimalFeederBlock();
    public static AnimalBabySeparatorBlock ANIMAL_BABY_SEPARATOR = new AnimalBabySeparatorBlock();
    public static MobCrusherBlock MOB_CRUSHER = new MobCrusherBlock();
    public static HydroponicBedBlock HYDROPONIC_BED = new HydroponicBedBlock();
    public static MobDuplicatorBlock MOB_DUPLICATOR = new MobDuplicatorBlock();
    public static WitherBuilderBlock WITHER_BUILDER = new WitherBuilderBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> builders = new ArrayList<>();
        builders.add(Feature.builder("crop_farming")
                .content(Block.class, PLANT_GATHERER)
                .event(EventManager.forge(RegistryEvent.NewRegistry.class).process(newRegistry -> IFRegistries.poke()))
                .content(PlantRecollectable.class, new BlockCropPlantRecollectable())
                .content(PlantRecollectable.class, new BlockNetherWartRecollectable())
                .content(PlantRecollectable.class, new DoubleTallPlantRecollectable())
                .content(PlantRecollectable.class, new PumpkinMelonPlantRecollectable())
                .content(PlantRecollectable.class, new TreePlantRecollectable())
                .content(PlantRecollectable.class, new ChorusFruitRecollectable())
                .content(PlantRecollectable.class, new BambooPlantRecollectable())
                .content(PlantRecollectable.class, new KelpPlantRecollectable())
                .content(PlantRecollectable.class, new SweetBerriesPlantRecollectable())
                .content(Block.class, PLANT_SOWER)
        );
        builders.add(Feature.builder("sewage").
                content(Block.class, SEWER).
                content(Block.class, SEWAGE_COMPOSTER));
        builders.add(createFeature(PLANT_FERTILIZER));
        builders.add(createFeature(SLAUGHTER_FACTORY));
        builders.add(createFeature(ANIMAL_RANCHER));
        builders.add(createFeature(ANIMAL_FEEDER));
        builders.add(createFeature(ANIMAL_BABY_SEPARATOR));
        builders.add(createFeature(MOB_CRUSHER));
        builders.add(createFeature(HYDROPONIC_BED));
        builders.add(createFeature(MOB_DUPLICATOR));
        builders.add(createFeature(WITHER_BUILDER));
        TAB_AG_HUS.addIconStack(new ItemStack(PLANT_SOWER));
        return builders;
    }
}
