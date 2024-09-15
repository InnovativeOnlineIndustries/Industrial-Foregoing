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
import com.buuz135.industrial.block.resourceproduction.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.resources.ResourceLocation;

;

public class ModuleResourceProduction implements IModule {

    public static TitaniumTab TAB_RESOURCE = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "resource_production"));

    public static BlockWithTile RESOURCEFUL_FURNACE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("resourceful_furnace", () -> new ResourcefulFurnaceBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile SLUDGE_REFINER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("sludge_refiner", () -> new SludgeRefinerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile WATER_CONDENSATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("water_condensator", () -> new WaterCondensatorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile MECHANICAL_DIRT = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("mechanical_dirt", () -> new MechanicalDirtBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile BLOCK_PLACER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("block_placer", () -> new BlockPlacerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile BLOCK_BREAKER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("block_breaker", () -> new BlockBreakerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile FLUID_COLLECTOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("fluid_collector", () -> new FluidCollectorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile FLUID_PLACER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("fluid_placer", () -> new FluidPlacerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile DYE_MIXER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("dye_mixer", () -> new DyeMixerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile SPORES_RECREATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("spores_recreator", () -> new SporesRecreatorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile MATERIAL_STONEWORK_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("material_stonework_factory", () -> new MaterialStoneWorkFactoryBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile MARINE_FISHER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("marine_fisher", () -> new MarineFisherBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile POTION_BREWER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("potion_brewer", () -> new PotionBrewerBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile ORE_LASER_BASE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("ore_laser_base", () -> new OreLaserBaseBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile LASER_DRILL = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("laser_drill", () -> new LaserDrillBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile FLUID_LASER_BASE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("fluid_laser_base", () -> new FluidLaserBaseBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile WASHING_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("washing_factory", () -> new WashingFactoryBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile FERMENTATION_STATION = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("fermentation_station", () -> new FermentationStationBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);
    public static BlockWithTile FLUID_SIEVING_MACHINE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("fluid_sieving_machine", () -> new FluidSievingMachineBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_RESOURCE), TAB_RESOURCE);

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
    }

}
