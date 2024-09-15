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
import com.buuz135.industrial.block.IndustrialBlockItem;
import com.buuz135.industrial.block.transportstorage.ConveyorBlock;
import com.buuz135.industrial.block.transportstorage.TransporterBlock;
import com.buuz135.industrial.block.transportstorage.conveyor.*;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterFluidType;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterItemType;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterWorldType;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.gui.transporter.ContainerTransporter;
import com.buuz135.industrial.item.ItemConveyorUpgrade;
import com.buuz135.industrial.item.ItemTransporterType;
import com.buuz135.industrial.proxy.client.model.ConveyorBlockModel;
import com.buuz135.industrial.proxy.client.model.TransporterBlockModel;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.HashMap;

public class ModuleTransportStorage implements IModule {

    public static TitaniumTab TAB_TRANSPORT = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "transport"));

    public static ConveyorUpgradeFactory upgrade_extraction = new ConveyorExtractionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_insertion = new ConveyorInsertionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_detector = new ConveyorDetectorUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_bouncing = new ConveyorBouncingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_dropping = new ConveyorDroppingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_blinking = new ConveyorBlinkingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_splitting = new ConveyorSplittingUpgrade.Factory();

    public static BlockWithTile CONVEYOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("conveyor", () -> new ConveyorBlock(TAB_TRANSPORT), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_TRANSPORT), TAB_TRANSPORT);
    public static HashMap<ResourceLocation, BakedModel> CONVEYOR_UPGRADES_CACHE = new HashMap<>();

    public static TransporterTypeFactory ITEM_TRANSPORTER = new TransporterItemType.Factory();
    public static TransporterTypeFactory FLUID_TRANSPORTER = new TransporterFluidType.Factory();
    public static TransporterTypeFactory WORLD_TRANSPORTER = new TransporterWorldType.Factory();

    public static BlockWithTile TRANSPORTER = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("transporter", () -> new TransporterBlock(), blockRegistryObject -> () -> new TransporterBlock.Item(blockRegistryObject.get(), TAB_TRANSPORT), TAB_TRANSPORT);
    public static HashMap<ResourceLocation, BakedModel> TRANSPORTER_CACHE = new HashMap<>();

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        ContainerConveyor.TYPE = registryHelper.registerGeneric(Registries.MENU, "conveyor", () -> IMenuTypeExtension.create(ContainerConveyor::new));
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> registryHelper.registerGeneric(Registries.ITEM, "conveyor_" + conveyorUpgradeFactory.getName() + "_upgrade", () -> new ItemConveyorUpgrade(conveyorUpgradeFactory, TAB_TRANSPORT)));
        ContainerTransporter.TYPE = registryHelper.registerGeneric(Registries.MENU, "transporter", () -> IMenuTypeExtension.create(ContainerTransporter::new));
        TransporterTypeFactory.FACTORIES.forEach(transporterTypeFactory -> registryHelper.registerGeneric(Registries.ITEM, transporterTypeFactory.getName() + "_transporter_type", () -> new ItemTransporterType(transporterTypeFactory, TAB_TRANSPORT)));
        if (FMLEnvironment.dist.isClient()) {
            this.onClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void bakingCompleted(ModelEvent.BakingCompleted event) {
        var models = event.getModels();
        var modelBakery = event.getModelBakery();
        for (TransporterTypeFactory transporterTypeFactory : TransporterTypeFactory.FACTORIES) {
            for (Direction upgradeFacing : transporterTypeFactory.getValidFacings()) {
                for (TransporterTypeFactory.TransporterAction actions : TransporterTypeFactory.TransporterAction.values()) {
                    try {
                        ResourceLocation resourceLocation = transporterTypeFactory.getModel(upgradeFacing, actions);
                        var modelResourceLocation = new ModelResourceLocation(resourceLocation, "standalone");
                        UnbakedModel unbakedModel = event.getModelBakery().getModel(resourceLocation);
                        ModelBaker baker = modelBakery.new ModelBakerImpl((modelLoc, material) -> material.sprite(), modelResourceLocation);
                        BakedModel bakedModel = unbakedModel.bake(baker, Material::sprite, new SimpleModelState(Transformation.identity()));
                        TRANSPORTER_CACHE.put(resourceLocation, bakedModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        for (ConveyorUpgradeFactory conveyorUpgradeFactory : ConveyorUpgradeFactory.FACTORIES) {
            for (Direction upgradeFacing : conveyorUpgradeFactory.getValidFacings()) {
                for (Direction conveyorFacing : ConveyorBlock.FACING.getPossibleValues()) {
                    try {
                        ResourceLocation resourceLocation = conveyorUpgradeFactory.getModel(upgradeFacing, conveyorFacing);
                        var modelResourceLocation = new ModelResourceLocation(resourceLocation, "standalone");
                        UnbakedModel unbakedModel = event.getModelBakery().getModel(resourceLocation);
                        ModelBaker baker = modelBakery.new ModelBakerImpl((modelLoc, material) -> material.sprite(), modelResourceLocation);
                        CONVEYOR_UPGRADES_CACHE.put(resourceLocation, unbakedModel.bake(baker, Material::sprite, new SimpleModelState(Transformation.identity())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        for (ModelResourceLocation resourceLocation : event.getModels().keySet()) {
            if (resourceLocation.id().getNamespace().equals(Reference.MOD_ID)) {
                if (resourceLocation.id().getPath().contains("transporter") && !resourceLocation.id().getPath().contains("transporters/") && !resourceLocation.id().getPath().contains("type")) {
                    event.getModels().put(resourceLocation, new TransporterBlockModel(event.getModels().get(resourceLocation)));
                }
                if (resourceLocation.id().getPath().contains("conveyor") && !resourceLocation.id().getPath().contains("upgrade")) {
                    event.getModels().put(resourceLocation, new ConveyorBlockModel(event.getModels().get(resourceLocation)));
                }
            }
        }
    }

    /*@OnlyIn(Dist.CLIENT)
    private void onClientSetupConveyor(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerConveyor.TYPE.get(), GuiConveyor::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetupTransporter(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTransporter.TYPE, GuiTransporter::new);
    }*/

    @OnlyIn(Dist.CLIENT)
    private void onClient() {
        //EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetupConveyor).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(this::bakingCompleted).subscribe();
        //EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch).subscribe();
        EventManager.mod(ModelEvent.ModifyBakingResult.class).process(this::modifyBakingResult).subscribe();
        //EventManager.mod(TextureStitchEvent.Pre.class).process(this::transporterTextureStitch).subscribe();
        //EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetupTransporter).subscribe();
    }
}
