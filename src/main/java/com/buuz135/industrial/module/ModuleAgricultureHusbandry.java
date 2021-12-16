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
import net.minecraftforge.registries.RegistryObject;

public class ModuleAgricultureHusbandry implements IModule {

    public static AdvancedTitaniumTab TAB_AG_HUS = new AdvancedTitaniumTab(Reference.MOD_ID + "_ag_hus", true);

    public static RegistryObject<Block> PLANT_GATHERER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "plant_gatherer", () ->  new PlantGathererBlock());
    public static RegistryObject<Block> SEWER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "sewer", () ->  new SewerBlock());
    public static RegistryObject<Block> SEWAGE_COMPOSTER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "sewage_composter", () ->  new SewageComposterBlock());
    public static RegistryObject<Block> PLANT_FERTILIZER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "plant_fertilizer", () ->  new PlantFertilizerBlock());
    public static RegistryObject<Block> PLANT_SOWER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "plant_sower", () ->  new PlantSowerBlock());
    public static RegistryObject<Block> SLAUGHTER_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "mob_slaughter_factory", () ->  new SlaughterFactoryBlock());
    public static RegistryObject<Block> ANIMAL_RANCHER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "animal_rancher", () ->  new AnimalRancherBlock());
    public static RegistryObject<Block> ANIMAL_FEEDER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "animal_feeder", () ->  new AnimalFeederBlock());
    public static RegistryObject<Block> ANIMAL_BABY_SEPARATOR = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "animal_baby_separator", () ->  new AnimalBabySeparatorBlock());
    public static RegistryObject<Block> MOB_CRUSHER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "mob_crusher", () ->  new MobCrusherBlock());
    public static RegistryObject<Block> HYDROPONIC_BED = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "hydroponic_bed", () ->  new HydroponicBedBlock());
    public static RegistryObject<Block> MOB_DUPLICATOR = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "mob_duplicator", () ->  new MobDuplicatorBlock());
    public static RegistryObject<Block> WITHER_BUILDER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "wither_builder", () ->  new WitherBuilderBlock());

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        registryHelper.register(PlantRecollectable.class,"blockcropplant", () -> new BlockCropPlantRecollectable());
        registryHelper.register(PlantRecollectable.class,"blocknetherwart", () -> new BlockNetherWartRecollectable());
        registryHelper.register(PlantRecollectable.class,"blocksugarandcactus", () -> new DoubleTallPlantRecollectable());
        registryHelper.register(PlantRecollectable.class,"blockpumpkingandmelon", () -> new PumpkinMelonPlantRecollectable());
        registryHelper.register(PlantRecollectable.class,"tree", () -> new TreePlantRecollectable());
        registryHelper.register(PlantRecollectable.class,"chorus_fruit", () -> new ChorusFruitRecollectable());
        registryHelper.register(PlantRecollectable.class,"bamboo", () -> new BambooPlantRecollectable());
        registryHelper.register(PlantRecollectable.class,"kelp", () -> new KelpPlantRecollectable());
        registryHelper.register(PlantRecollectable.class,"sweetberries", () -> new SweetBerriesPlantRecollectable());
        TAB_AG_HUS.addIconStack(() -> new ItemStack(PLANT_SOWER.orElse(Blocks.STONE)));
    }
}
