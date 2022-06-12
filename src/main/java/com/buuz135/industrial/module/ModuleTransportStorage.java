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
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.*;
import com.buuz135.industrial.block.transportstorage.conveyor.*;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterFluidType;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterItemType;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterWorldType;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.gui.transporter.ContainerTransporter;
import com.buuz135.industrial.gui.transporter.GuiTransporter;
import com.buuz135.industrial.item.ItemConveyorUpgrade;
import com.buuz135.industrial.item.ItemTransporterType;
import com.buuz135.industrial.proxy.client.model.ConveyorBlockModel;
import com.buuz135.industrial.proxy.client.model.TransporterBlockModel;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class ModuleTransportStorage implements IModule {

    public static AdvancedTitaniumTab TAB_TRANSPORT = new AdvancedTitaniumTab(Reference.MOD_ID + "_transport", true);

    public static ConveyorUpgradeFactory upgrade_extraction = new ConveyorExtractionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_insertion = new ConveyorInsertionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_detector = new ConveyorDetectorUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_bouncing = new ConveyorBouncingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_dropping = new ConveyorDroppingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_blinking = new ConveyorBlinkingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_splitting = new ConveyorSplittingUpgrade.Factory();

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> CONVEYOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("conveyor", () -> new ConveyorBlock(TAB_TRANSPORT));
    public static HashMap<ResourceLocation, BakedModel> CONVEYOR_UPGRADES_CACHE = new HashMap<>();

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_UNIT_COMMON = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(Rarity.COMMON.name().toLowerCase() + "_black_hole_unit", () -> new BlackHoleUnitBlock(Rarity.COMMON), blockRegistryObject -> () -> new BlackHoleUnitBlock.BlackHoleUnitItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), Rarity.COMMON));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_UNIT_PITY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.PITY_RARITY.name().toLowerCase() + "_black_hole_unit", () -> new BlackHoleUnitBlock(ModuleCore.PITY_RARITY), blockRegistryObject -> () -> new BlackHoleUnitBlock.BlackHoleUnitItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.PITY_RARITY));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_UNIT_SIMPLE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.SIMPLE_RARITY.name().toLowerCase() + "_black_hole_unit", () -> new BlackHoleUnitBlock(ModuleCore.SIMPLE_RARITY), blockRegistryObject -> () -> new BlackHoleUnitBlock.BlackHoleUnitItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.SIMPLE_RARITY));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_UNIT_ADVANCED = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.ADVANCED_RARITY.name().toLowerCase() + "_black_hole_unit", () -> new BlackHoleUnitBlock(ModuleCore.ADVANCED_RARITY), blockRegistryObject -> () -> new BlackHoleUnitBlock.BlackHoleUnitItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.ADVANCED_RARITY));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_UNIT_SUPREME = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.SUPREME_RARITY.name().toLowerCase() + "_black_hole_unit", () -> new BlackHoleUnitBlock(ModuleCore.SUPREME_RARITY), blockRegistryObject -> () -> new BlackHoleUnitBlock.BlackHoleUnitItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.SUPREME_RARITY));

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_TANK_COMMON = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(Rarity.COMMON.name().toLowerCase() + "_black_hole_tank", () -> new BlackHoleTankBlock(Rarity.COMMON), blockRegistryObject -> () -> new BlackHoleTankBlock.BlackHoleTankItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT),  Rarity.COMMON));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_TANK_PITY = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.PITY_RARITY.name().toLowerCase() + "_black_hole_tank", () -> new BlackHoleTankBlock(ModuleCore.PITY_RARITY), blockRegistryObject -> () -> new BlackHoleTankBlock.BlackHoleTankItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.PITY_RARITY));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_TANK_SIMPLE = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.SIMPLE_RARITY.name().toLowerCase() + "_black_hole_tank", () -> new BlackHoleTankBlock(ModuleCore.SIMPLE_RARITY), blockRegistryObject -> () -> new BlackHoleTankBlock.BlackHoleTankItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.SIMPLE_RARITY));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_TANK_ADVANCED = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.ADVANCED_RARITY.name().toLowerCase() + "_black_hole_tank", () -> new BlackHoleTankBlock(ModuleCore.ADVANCED_RARITY), blockRegistryObject -> () -> new BlackHoleTankBlock.BlackHoleTankItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.ADVANCED_RARITY));
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_TANK_SUPREME = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem(ModuleCore.SUPREME_RARITY.name().toLowerCase() + "_black_hole_tank", () -> new BlackHoleTankBlock(ModuleCore.SUPREME_RARITY), blockRegistryObject -> () -> new BlackHoleTankBlock.BlackHoleTankItem(blockRegistryObject.get(), new Item.Properties().tab(TAB_TRANSPORT), ModuleCore.SUPREME_RARITY));

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> BLACK_HOLE_CONTROLLER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("black_hole_controller", () -> new BlackHoleControllerBlock());


    public static TransporterTypeFactory ITEM_TRANSPORTER = new TransporterItemType.Factory();
    public static TransporterTypeFactory FLUID_TRANSPORTER = new TransporterFluidType.Factory();
    public static TransporterTypeFactory WORLD_TRANSPORTER = new TransporterWorldType.Factory();

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> TRANSPORTER =  IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTile("transporter", () -> new TransporterBlock(TAB_TRANSPORT));
    public static HashMap<ResourceLocation, BakedModel> TRANSPORTER_CACHE = new HashMap<>();

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        TAB_TRANSPORT.addIconStack(() -> new ItemStack(CONVEYOR.getLeft().orElse(Blocks.STONE)));
        registryHelper.registerGeneric(MenuType.class, "conveyor", () ->  (MenuType) IForgeMenuType.create(ContainerConveyor::new));
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> registryHelper.registerGeneric(Item.class,"conveyor_" + conveyorUpgradeFactory.getRegistryName().getPath() + "_upgrade", () -> new ItemConveyorUpgrade(conveyorUpgradeFactory, TAB_TRANSPORT)));
        registryHelper.registerGeneric(MenuType.class, "transporter", () -> (MenuType) IForgeMenuType.create(ContainerTransporter::new));
        TransporterTypeFactory.FACTORIES.forEach(transporterTypeFactory -> registryHelper.registerGeneric(Item.class, transporterTypeFactory.getRegistryName().getPath() + "_transporter_type", () ->  new ItemTransporterType(transporterTypeFactory, TAB_TRANSPORT)));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onClient);

    }

    @OnlyIn(Dist.CLIENT)
    private void conveyorBake(ModelBakeEvent event) {
        for (ResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
            if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                if (resourceLocation.getPath().contains("conveyor") && !resourceLocation.getPath().contains("upgrade"))
                    event.getModelRegistry().put(resourceLocation, new ConveyorBlockModel(event.getModelRegistry().get(resourceLocation)));
            }
        }
        for (ConveyorUpgradeFactory conveyorUpgradeFactory : ConveyorUpgradeFactory.FACTORIES) {
            for (Direction upgradeFacing : conveyorUpgradeFactory.getValidFacings()) {
                for (Direction conveyorFacing : ConveyorBlock.FACING.getPossibleValues()) {
                    try {
                        ResourceLocation resourceLocation = conveyorUpgradeFactory.getModel(upgradeFacing, conveyorFacing);
                        UnbakedModel unbakedModel = event.getModelLoader().getModel(resourceLocation);
                        CONVEYOR_UPGRADES_CACHE.put(resourceLocation, unbakedModel.bake(event.getModelLoader(), ForgeModelBakery.defaultTextureGetter(), new SimpleModelState(ImmutableMap.of()), resourceLocation));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void textureStitch(TextureStitchEvent.Pre pre) {
        if (pre.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
            ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.getTextures().forEach(pre::addSprite));
    }

    @OnlyIn(Dist.CLIENT)
    private void transporterBake(ModelBakeEvent event) {
        for (ResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
            if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                if (resourceLocation.getPath().contains("transporter") && !resourceLocation.getPath().contains("transporters/") && !resourceLocation.getPath().contains("type"))
                    event.getModelRegistry().put(resourceLocation, new TransporterBlockModel(event.getModelRegistry().get(resourceLocation)));
            }
        }
        for (TransporterTypeFactory transporterTypeFactory : TransporterTypeFactory.FACTORIES) {
            String itemRL = Reference.MOD_ID + ":" + transporterTypeFactory.getRegistryName().getPath() + "_transporter_type#inventory";
            for (Direction upgradeFacing : transporterTypeFactory.getValidFacings()) {
                for (TransporterTypeFactory.TransporterAction actions : TransporterTypeFactory.TransporterAction.values()) {
                    try {
                        ResourceLocation resourceLocation = transporterTypeFactory.getModel(upgradeFacing, actions);
                        UnbakedModel unbakedModel = event.getModelLoader().getModel(resourceLocation);
                        TRANSPORTER_CACHE.put(resourceLocation, unbakedModel.bake(event.getModelLoader(), ForgeModelBakery.defaultTextureGetter(), new SimpleModelState(ImmutableMap.of()), resourceLocation));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (ResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
                if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                    if (resourceLocation.toString().equals(itemRL))
                        event.getModelRegistry().put(resourceLocation, TRANSPORTER_CACHE.get(transporterTypeFactory.getItemModel()));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void transporterTextureStitch(TextureStitchEvent.Pre pre) {
        if (pre.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
            TransporterTypeFactory.FACTORIES.forEach(transporterTypeFactory -> transporterTypeFactory.getTextures().forEach(pre::addSprite));
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetupConveyor(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerConveyor.TYPE, GuiConveyor::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetupTransporter(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTransporter.TYPE, GuiTransporter::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClient(){
        EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetupConveyor).subscribe();
        EventManager.mod(ModelBakeEvent.class).process(this::conveyorBake).subscribe();
        EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch).subscribe();
        EventManager.mod(ModelBakeEvent.class).process(this::transporterBake).subscribe();
        EventManager.mod(TextureStitchEvent.Pre.class).process(this::transporterTextureStitch).subscribe();
        EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetupTransporter).subscribe();
    }
}
