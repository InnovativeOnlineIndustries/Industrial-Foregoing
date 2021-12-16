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
import net.minecraftforge.registries.RegistryObject;

;

public class ModuleResourceProduction implements IModule {

    public static AdvancedTitaniumTab TAB_RESOURCE = new AdvancedTitaniumTab(Reference.MOD_ID + "_resource_production", true);

    public static RegistryObject<Block> RESOURCEFUL_FURNACE = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "resourceful_furnace", () -> new ResourcefulFurnaceBlock());
    public static RegistryObject<Block> SLUDGE_REFINER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "sludge_refiner", () ->  new SludgeRefinerBlock());
    public static RegistryObject<Block> WATER_CONDENSATOR = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "water_condensator", () ->  new WaterCondensatorBlock());
    public static RegistryObject<Block> MECHANICAL_DIRT = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "mechanical_dirt", () ->  new MechanicalDirtBlock());
    public static RegistryObject<Block> BLOCK_PLACER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "block_placer", () ->  new BlockPlacerBlock());
    public static RegistryObject<Block> BLOCK_BREAKER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "block_breaker", () ->  new BlockBreakerBlock());
    public static RegistryObject<Block> FLUID_COLLECTOR = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "fluid_collector", () ->  new FluidCollectorBlock());
    public static RegistryObject<Block> FLUID_PLACER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "fluid_placer", () ->  new FluidPlacerBlock());
    public static RegistryObject<Block> DYE_MIXER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "dye_mixer", () ->  new DyeMixerBlock());
    public static RegistryObject<Block> SPORES_RECREATOR = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "spores_recreator", () ->  new SporesRecreatorBlock());
    public static RegistryObject<Block> MATERIAL_STONEWORK_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "material_stonework_factory", () ->  new MaterialStoneWorkFactoryBlock());
    public static RegistryObject<Block> MARINE_FISHER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "marine_fisher", () ->  new MarineFisherBlock());
    public static RegistryObject<Block> POTION_BREWER = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "potion_brewer", () ->  new PotionBrewerBlock());
    public static RegistryObject<Block> ORE_LASER_BASE = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "ore_laser_base", () ->  new OreLaserBaseBlock());
    public static RegistryObject<Block> LASER_DRILL = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "laser_drill", () ->  new LaserDrillBlock());
    public static RegistryObject<Block> FLUID_LASER_BASE = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "fluid_laser_base", () ->  new FluidLaserBaseBlock());
    public static RegistryObject<Block> WASHING_FACTORY = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "washing_factory", () ->  new WashingFactoryBlock());
    public static RegistryObject<Block> FERMENTATION_STATION = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "fermentation_station", () ->  new FermentationStationBlock());
    public static RegistryObject<Block> FLUID_SIEVING_MACHINE = IndustrialForegoing.INSTANCE.getRegistries().register(Block.class, "fluid_sieving_machine", () ->  new FluidSievingMachineBlock());

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        TAB_RESOURCE.addIconStack(() -> new ItemStack(WATER_CONDENSATOR.orElse(Blocks.STONE)));
    }

}
