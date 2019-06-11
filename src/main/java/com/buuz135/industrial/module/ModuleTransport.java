package com.buuz135.industrial.module;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.item.ItemConveyorUpgrade;
import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.buuz135.industrial.proxy.block.upgrade.*;
import com.buuz135.industrial.proxy.client.model.ConveyorBlockModel;
import com.buuz135.industrial.proxy.network.ConveyorButtonInteractMessage;
import com.buuz135.industrial.proxy.network.ConveyorSplittingSyncEntityMessage;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleTransport implements IModule {

    public static ConveyorUpgradeFactory upgrade_extraction = new ConveyorExtractionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_insertion = new ConveyorInsertionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_detector = new ConveyorDetectorUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_bouncing = new ConveyorBouncingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_dropping = new ConveyorDroppingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_blinking = new ConveyorBlinkingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_splitting = new ConveyorSplittingUpgrade.Factory();

    public static BlockConveyor CONVEYOR;
    public static HashMap<ResourceLocation, IBakedModel> CONVEYOR_UPGRADES_CACHE = new HashMap<>();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("conveyor").content(Block.class, CONVEYOR = new BlockConveyor()));
        Feature.Builder builder = Feature.builder("conveyor_upgrades")
                .event(EventManager.forge(ModelBakeEvent.class).process(this::conveyorBake))
                .event(EventManager.forge(TextureStitchEvent.Pre.class).process(this::textureStitch))
                .event(EventManager.mod(FMLCommonSetupEvent.class).process(fmlCommonSetupEvent -> {
                    NetworkHandler.registerMessage(ConveyorButtonInteractMessage.class);
                    NetworkHandler.registerMessage(ConveyorSplittingSyncEntityMessage.class);
                }));
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> builder.content(Item.class, new ItemConveyorUpgrade(conveyorUpgradeFactory)));
        features.add(builder);
        return features;
    }

    @OnlyIn(Dist.CLIENT)
    private void conveyorBake(ModelBakeEvent event) {
        for (ModelResourceLocation resourceLocation : event.getModelRegistry().keySet()) {
            if (resourceLocation.getNamespace().equals(Reference.MOD_ID)) {
                if (resourceLocation.getPath().contains("conveyor") && !resourceLocation.getPath().contains("upgrade"))
                    event.getModelRegistry().put(resourceLocation, new ConveyorBlockModel(event.getModelRegistry().get(resourceLocation)));
            }
        }
        for (ConveyorUpgradeFactory conveyorUpgradeFactory : ConveyorUpgradeFactory.FACTORIES) {
            for (EnumFacing upgradeFacing : conveyorUpgradeFactory.getValidFacings()) {
                for (EnumFacing conveyorFacing : BlockConveyor.FACING.getAllowedValues()) {
                    try {
                        ResourceLocation resourceLocation = conveyorUpgradeFactory.getModel(upgradeFacing, conveyorFacing);
                        IUnbakedModel unbakedModel = event.getModelLoader().getUnbakedModel(resourceLocation);
                        CONVEYOR_UPGRADES_CACHE.put(resourceLocation, unbakedModel.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), TRSRTransformation.identity(), false, DefaultVertexFormats.BLOCK));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void textureStitch(TextureStitchEvent.Pre pre) {
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.getTextures().forEach(resourceLocation -> pre.getMap().registerSprite(Minecraft.getInstance().getResourceManager(), resourceLocation)));
    }
}
