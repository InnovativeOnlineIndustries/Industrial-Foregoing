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
import com.buuz135.industrial.block.resourceproduction.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

;

public class ModuleResourceProduction implements IModule {

    public static AdvancedTitaniumTab TAB_RESOURCE = new AdvancedTitaniumTab(Reference.MOD_ID + "_resource_production", true);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> RESOURCEFUL_FURNACE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("resourceful_furnace", () -> new ResourcefulFurnaceBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SLUDGE_REFINER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("sludge_refiner", () ->  new SludgeRefinerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> WATER_CONDENSATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("water_condensator", () ->  new WaterCondensatorBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MECHANICAL_DIRT = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("mechanical_dirt", () ->  new MechanicalDirtBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLOCK_PLACER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("block_placer", () ->  new BlockPlacerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLOCK_BREAKER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("block_breaker", () ->  new BlockBreakerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> FLUID_COLLECTOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("fluid_collector", () ->  new FluidCollectorBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> FLUID_PLACER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("fluid_placer", () ->  new FluidPlacerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> DYE_MIXER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("dye_mixer", () ->  new DyeMixerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SPORES_RECREATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("spores_recreator", () ->  new SporesRecreatorBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MATERIAL_STONEWORK_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("material_stonework_factory", () ->  new MaterialStoneWorkFactoryBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MARINE_FISHER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("marine_fisher", () ->  new MarineFisherBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> POTION_BREWER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("potion_brewer", () ->  new PotionBrewerBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> ORE_LASER_BASE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("ore_laser_base", () ->  new OreLaserBaseBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> LASER_DRILL = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("laser_drill", () ->  new LaserDrillBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> FLUID_LASER_BASE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("fluid_laser_base", () ->  new FluidLaserBaseBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> WASHING_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("washing_factory", () ->  new WashingFactoryBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> FERMENTATION_STATION = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("fermentation_station", () ->  new FermentationStationBlock());
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> FLUID_SIEVING_MACHINE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("fluid_sieving_machine", () ->  new FluidSievingMachineBlock());

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        TAB_RESOURCE.addIconStack(() -> new ItemStack(WATER_CONDENSATOR.getLeft().orElse(Blocks.STONE)));
    }

}
