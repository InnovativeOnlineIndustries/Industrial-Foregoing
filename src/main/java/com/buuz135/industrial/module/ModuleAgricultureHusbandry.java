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
import com.buuz135.industrial.block.agriculturehusbandry.*;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.apihandlers.plant.*;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.resources.ResourceLocation;


public class ModuleAgricultureHusbandry implements IModule {

    public static TitaniumTab TAB_AG_HUS = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ag_hus"));

    public static BlockWithTile PLANT_GATHERER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("plant_gatherer", () -> new PlantGathererBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile SEWER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("sewer", () -> new SewerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile SEWAGE_COMPOSTER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("sewage_composter", () -> new SewageComposterBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile PLANT_FERTILIZER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("plant_fertilizer", () -> new PlantFertilizerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile PLANT_SOWER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("plant_sower", () -> new PlantSowerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile SLAUGHTER_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("mob_slaughter_factory", () -> new SlaughterFactoryBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile ANIMAL_RANCHER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("animal_rancher", () -> new AnimalRancherBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile ANIMAL_FEEDER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("animal_feeder", () -> new AnimalFeederBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile ANIMAL_BABY_SEPARATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("animal_baby_separator", () -> new AnimalBabySeparatorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile MOB_CRUSHER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("mob_crusher", () -> new MobCrusherBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile HYDROPONIC_BED = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("hydroponic_bed", () -> new HydroponicBedBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile MOB_DUPLICATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("mob_duplicator", () -> new MobDuplicatorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);
    public static BlockWithTile WITHER_BUILDER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("wither_builder", () -> new WitherBuilderBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_AG_HUS), TAB_AG_HUS);

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "blockcropplant", BlockCropPlantRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "blocknetherwart", BlockNetherWartRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "blocksugarandcactus", DoubleTallPlantRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "blockpumpkingandmelon", PumpkinMelonPlantRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "tree", TreePlantRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "chorus_fruit", ChorusFruitRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "bamboo", BambooPlantRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "kelp", KelpPlantRecollectable::new);
        registryHelper.registerGeneric(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY_KEY, "sweetberries", SweetBerriesPlantRecollectable::new);
    }
}
