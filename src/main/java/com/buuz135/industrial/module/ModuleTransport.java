package com.buuz135.industrial.module;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.item.ItemConveyorUpgrade;
import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.buuz135.industrial.proxy.block.upgrade.*;
import com.buuz135.industrial.proxy.client.model.ConveyorBlockModel;
import com.buuz135.industrial.proxy.network.ConveyorButtonInteractMessage;
import com.buuz135.industrial.proxy.network.ConveyorSplittingSyncEntityMessage;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ModuleTransport implements IModule {

    public static AdvancedTitaniumTab TAB_TRANSPORT = new AdvancedTitaniumTab(Reference.MOD_ID + "_transport", true);

    public static ConveyorUpgradeFactory upgrade_extraction = new ConveyorExtractionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_insertion = new ConveyorInsertionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_detector = new ConveyorDetectorUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_bouncing = new ConveyorBouncingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_dropping = new ConveyorDroppingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_blinking = new ConveyorBlinkingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_splitting = new ConveyorSplittingUpgrade.Factory();

    public static BlockConveyor CONVEYOR = new BlockConveyor(TAB_TRANSPORT);
    public static HashMap<ResourceLocation, IBakedModel> CONVEYOR_UPGRADES_CACHE = new HashMap<>();

    @Override
    public List<Feature.Builder> generateFeatures() {
        TAB_TRANSPORT.addIconStack(new ItemStack(CONVEYOR));
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("conveyor")
                .content(Block.class, CONVEYOR)
                .content(ContainerType.class, (ContainerType) IForgeContainerType.create(ContainerConveyor::new).setRegistryName(new ResourceLocation(Reference.MOD_ID, "conveyor")))
                .eventClient(() -> () -> EventManager.mod(FMLClientSetupEvent.class).process(this::onClientSetup))
        );
        Feature.Builder builder = Feature.builder("conveyor_upgrades")
                .eventClient(() -> () -> EventManager.mod(ModelBakeEvent.class).process(this::conveyorBake))
                .eventClient(() -> () -> EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch))
                .event(EventManager.mod(FMLCommonSetupEvent.class).process(fmlCommonSetupEvent -> {
                    NetworkHandler.registerMessage(ConveyorButtonInteractMessage.class);
                    NetworkHandler.registerMessage(ConveyorSplittingSyncEntityMessage.class);
                }));
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> builder.content(Item.class, new ItemConveyorUpgrade(conveyorUpgradeFactory, TAB_TRANSPORT)));
        features.add(builder);
        return features;
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
                for (Direction conveyorFacing : BlockConveyor.FACING.getAllowedValues()) {
                    try {
                        ResourceLocation resourceLocation = conveyorUpgradeFactory.getModel(upgradeFacing, conveyorFacing);
                        IUnbakedModel unbakedModel = event.getModelLoader().getUnbakedModel(resourceLocation);
                        CONVEYOR_UPGRADES_CACHE.put(resourceLocation, unbakedModel.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new SimpleModelState(ImmutableMap.of(), Optional.of(TRSRTransformation.identity())), DefaultVertexFormats.BLOCK));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void textureStitch(TextureStitchEvent.Pre pre) {
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.getTextures().forEach(pre::addSprite));
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ContainerConveyor.TYPE, GuiConveyor::new);
    }
}
