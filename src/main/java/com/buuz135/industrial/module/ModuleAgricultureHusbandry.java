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
import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.agriculturehusbandry.*;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.apihandlers.plant.*;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

public class ModuleAgricultureHusbandry implements IModule {

    public static AdvancedTitaniumTab TAB_AG_HUS = new AdvancedTitaniumTab(Reference.MOD_ID + "_ag_hus", true);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> PLANT_GATHERER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("plant_gatherer", () ->  new PlantGathererBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SEWER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("sewer", () ->  new SewerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SEWAGE_COMPOSTER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("sewage_composter", () ->  new SewageComposterBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> PLANT_FERTILIZER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("plant_fertilizer", () ->  new PlantFertilizerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> PLANT_SOWER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("plant_sower", () ->  new PlantSowerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SLAUGHTER_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("mob_slaughter_factory", () ->  new SlaughterFactoryBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> ANIMAL_RANCHER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("animal_rancher", () ->  new AnimalRancherBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> ANIMAL_FEEDER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("animal_feeder", () ->  new AnimalFeederBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> ANIMAL_BABY_SEPARATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("animal_baby_separator", () ->  new AnimalBabySeparatorBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MOB_CRUSHER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("mob_crusher", () ->  new MobCrusherBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> HYDROPONIC_BED = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("hydroponic_bed", () ->  new HydroponicBedBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MOB_DUPLICATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("mob_duplicator", () ->  new MobDuplicatorBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> WITHER_BUILDER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("wither_builder", () ->  new WitherBuilderBlock());

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        registryHelper.registerGeneric(PlantRecollectable.class,"blockcropplant", () -> new BlockCropPlantRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"blocknetherwart", () -> new BlockNetherWartRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"blocksugarandcactus", () -> new DoubleTallPlantRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"blockpumpkingandmelon", () -> new PumpkinMelonPlantRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"tree", () -> new TreePlantRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"chorus_fruit", () -> new ChorusFruitRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"bamboo", () -> new BambooPlantRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"kelp", () -> new KelpPlantRecollectable());
        registryHelper.registerGeneric(PlantRecollectable.class,"sweetberries", () -> new SweetBerriesPlantRecollectable());
        TAB_AG_HUS.addIconStack(() -> new ItemStack(PLANT_SOWER.getLeft().orElse(Blocks.STONE)));
    }
}
