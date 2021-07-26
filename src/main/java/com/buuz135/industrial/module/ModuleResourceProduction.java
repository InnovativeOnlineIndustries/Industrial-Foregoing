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

import com.buuz135.industrial.block.resourceproduction.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleResourceProduction implements IModule {

    public static AdvancedTitaniumTab TAB_RESOURCE = new AdvancedTitaniumTab(Reference.MOD_ID + "_resource_production", true);

    public static ResourcefulFurnaceBlock RESOURCEFUL_FURNACE = new ResourcefulFurnaceBlock();
    public static SludgeRefinerBlock SLUDGE_REFINER = new SludgeRefinerBlock();
    public static WaterCondensatorBlock WATER_CONDENSATOR = new WaterCondensatorBlock();
    public static MechanicalDirtBlock MECHANICAL_DIRT = new MechanicalDirtBlock();
    public static BlockPlacerBlock BLOCK_PLACER = new BlockPlacerBlock();
    public static BlockBreakerBlock BLOCK_BREAKER = new BlockBreakerBlock();
    public static FluidCollectorBlock FLUID_COLLECTOR = new FluidCollectorBlock();
    public static FluidPlacerBlock FLUID_PLACER = new FluidPlacerBlock();
    public static DyeMixerBlock DYE_MIXER = new DyeMixerBlock();
    public static SporesRecreatorBlock SPORES_RECREATOR = new SporesRecreatorBlock();
    public static MaterialStoneWorkFactoryBlock MATERIAL_STONEWORK_FACTORY = new MaterialStoneWorkFactoryBlock();
    public static MarineFisherBlock MARINE_FISHER = new MarineFisherBlock();
    public static PotionBrewerBlock POTION_BREWER = new PotionBrewerBlock();
    public static OreLaserBaseBlock ORE_LASER_BASE = new OreLaserBaseBlock();
    public static LaserDrillBlock LASER_DRILL = new LaserDrillBlock();
    public static FluidLaserBaseBlock FLUID_LASER_BASE = new FluidLaserBaseBlock();
    public static WashingFactoryBlock WASHING_FACTORY = new WashingFactoryBlock();
    public static FermentationStationBlock FERMENTATION_STATION = new FermentationStationBlock();
    public static FluidSievingMachineBlock FLUID_SIEVING_MACHINE = new FluidSievingMachineBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(createFeature(RESOURCEFUL_FURNACE));
        features.add(createFeature(SLUDGE_REFINER));
        features.add(createFeature(WATER_CONDENSATOR));
        features.add(createFeature(MECHANICAL_DIRT));
        features.add(createFeature(BLOCK_PLACER));
        features.add(createFeature(BLOCK_BREAKER));
        features.add(createFeature(FLUID_COLLECTOR));
        features.add(createFeature(FLUID_PLACER));
        features.add(createFeature(DYE_MIXER));
        features.add(createFeature(SPORES_RECREATOR));
        features.add(createFeature(MATERIAL_STONEWORK_FACTORY));
        features.add(createFeature(MARINE_FISHER));
        features.add(createFeature(POTION_BREWER));
        features.add(createFeature(ORE_LASER_BASE));
        features.add(createFeature(LASER_DRILL));
        features.add(createFeature(FLUID_LASER_BASE));
        features.add(createFeature(WASHING_FACTORY));
        features.add(createFeature(FERMENTATION_STATION));
        features.add(createFeature(FLUID_SIEVING_MACHINE));
        TAB_RESOURCE.addIconStack(new ItemStack(WATER_CONDENSATOR));
        return features;
    }

}
