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

package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.buuz135.industrial.entity.InfinityNukeEntity;
import com.buuz135.industrial.entity.InfinityTridentEntity;
import com.buuz135.industrial.entity.client.*;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.*;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.event.IFClientEvents;
import com.buuz135.industrial.proxy.client.render.*;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.BlockWithTile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Calendar;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    public static ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/machines.png");
    public static BakedModel ears_baked;

    public static KeyMapping OPEN_BACKPACK;

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        registerAreaRender(event, ModuleCore.FLUID_EXTRACTOR);
        registerAreaRender(event, ModuleAgricultureHusbandry.PLANT_GATHERER);
        registerAreaRender(event, ModuleAgricultureHusbandry.PLANT_SOWER);
        registerAreaRender(event, ModuleAgricultureHusbandry.SEWER);
        registerAreaRender(event, ModuleAgricultureHusbandry.PLANT_FERTILIZER);
        registerAreaRender(event, ModuleAgricultureHusbandry.SLAUGHTER_FACTORY);
        registerAreaRender(event, ModuleAgricultureHusbandry.ANIMAL_RANCHER);
        registerAreaRender(event, ModuleAgricultureHusbandry.ANIMAL_FEEDER);
        registerAreaRender(event, ModuleAgricultureHusbandry.ANIMAL_BABY_SEPARATOR);
        registerAreaRender(event, ModuleAgricultureHusbandry.MOB_CRUSHER);
        registerAreaRender(event, ModuleAgricultureHusbandry.WITHER_BUILDER);
        registerAreaRender(event, ModuleMisc.STASIS_CHAMBER);
        registerAreaRender(event, ModuleResourceProduction.LASER_DRILL);
        registerAreaRender(event, ModuleAgricultureHusbandry.MOB_DUPLICATOR);

        event.registerBlockEntityRenderer((BlockEntityType<? extends MycelialReactorTile>) ModuleGenerator.MYCELIAL_REACTOR.type().get(), MycelialReactorTESR::new);

        event.registerEntityRenderer((EntityType<? extends InfinityTridentEntity>) ModuleTool.TRIDENT_ENTITY_TYPE.value(), InfinityTridentRenderer::new);
        event.registerEntityRenderer((EntityType<? extends InfinityNukeEntity>) ModuleTool.INFINITY_NUKE_ENTITY_TYPE.value(), InfinityNukeRenderer::new);
        event.registerEntityRenderer((EntityType<? extends InfinityLauncherProjectileEntity>) ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE.value(), InfinityLauncherProjectileRenderer::new);

        event.registerBlockEntityRenderer(((BlockEntityType<? extends TransporterTile>) ModuleTransportStorage.TRANSPORTER.type().get()), TransporterTESR::new);

        event.registerBlockEntityRenderer(((BlockEntityType<? extends ConveyorTile>) ModuleTransportStorage.CONVEYOR.type().get()), FluidConveyorTESR::new);
    }

    private static void registerAreaRender(EntityRenderersEvent.RegisterRenderers event, BlockWithTile pair) {
        event.registerBlockEntityRenderer((BlockEntityType<? extends IndustrialAreaWorkingTile>) pair.type().get(), WorkingAreaTESR::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            var renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new ContributorsCatEarsRender(playerRenderer));
                playerRenderer.addLayer(new InfinityLauncherProjectileArmorLayer(playerRenderer));
            }
        }
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(InfinityTridentRenderer.TRIDENT_LAYER, InfinityTridentModel::createBodyLayer);
        event.registerLayerDefinition(InfinityNukeRenderer.NUKE_LAYER, InfinityNukeModel::createBodyLayer);
        event.registerLayerDefinition(InfinityNukeRenderer.NUKE_ARMED_LAYER, () -> InfinityNukeModelArmed.createBodyLayer(new CubeDeformation(0f)));
        event.registerLayerDefinition(InfinityNukeRenderer.NUKE_ARMED_BIG_LAYER, () -> InfinityNukeModelArmed.createBodyLayer(new CubeDeformation(0.2f)));
        event.registerLayerDefinition(InfinityLauncherProjectileRenderer.PROJECTILE_LAYER, InfinityLauncherProjectileModel::createBodyLayer);
    }

    @Override
    public void run() {

        NeoForge.EVENT_BUS.register(new IFClientEvents());

        //((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> FluidUtils.colorCache.clear());

        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.CONVEYOR.getBlock(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTool.INFINITY_BACKPACK_BLOCK.getBlock(), RenderType.cutoutMipped());

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null) {
                BlockEntity entity = worldIn.getBlockEntity(pos);
                if (entity instanceof ConveyorTile) {
                    return ((ConveyorTile) entity).getColor();
                }
            }
            return 0xFFFFFFFF;
        }, ModuleTransportStorage.CONVEYOR.getBlock());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 || tintIndex == 2 || tintIndex == 3) {
                SpawnEggItem info = null;
                var blacklisted = false;
                if (stack.has(IFAttachments.MOB_IMPRISONMENT_TOOL)) {
                    var tag = stack.get(IFAttachments.MOB_IMPRISONMENT_TOOL);
                    ResourceLocation id = ResourceLocation.parse(tag.getString("entity"));
                    var type = BuiltInRegistries.ENTITY_TYPE.get(id);
                    info = SpawnEggItem.byId(type);
                    blacklisted = ((MobImprisonmentToolItem) ModuleTool.MOB_IMPRISONMENT_TOOL.get()).isBlacklisted(type);
                }
                return info == null ? 0xFF636363 : tintIndex == 3 ? blacklisted ? 0xFFDB201A : 0xFF636363 : FastColor.ARGB32.opaque(info.getColor(tintIndex - 1));
            }
            return 0xFFFFFFFF;
        }, ModuleTool.MOB_IMPRISONMENT_TOOL.get());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return FastColor.ARGB32.opaque(InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor());
            }
            return 0xFFFFFFFF;
        }, ModuleTool.INFINITY_BACKPACK.get(), ModuleTool.INFINITY_LAUNCHER.get(), ModuleTool.INFINITY_NUKE.get(), ModuleTool.INFINITY_TRIDENT.get(), ModuleTool.INFINITY_HAMMER.get(), ModuleTool.INFINITY_SAW.get(), ModuleTool.INFINITY_DRILL.get());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            var fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (tintIndex == 1 && fluidHandlerItem != null) {
                if (fluidHandlerItem.getFluidInTank(0).getAmount() > 0) {
                    int color = FluidUtils.getFluidColor(fluidHandlerItem.getFluidInTank(0));
                    if (color != -1) return FastColor.ARGB32.opaque(color);
                }
            }
            return 0xFFFFFFFF;
        }, ModuleCore.RAW_ORE_MEAT.getBucketFluid(), ModuleCore.FERMENTED_ORE_MEAT.getBucketFluid());

        EventManager.forge(ItemTooltipEvent.class).filter(event -> BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).getNamespace().equals(Reference.MOD_ID)).process(event -> {
            if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL) {
                event.getToolTip().add(Component.literal("Press Alt + F4 to cheat this item").withStyle(ChatFormatting.DARK_AQUA));
            }
        }).subscribe();

        Minecraft instance = Minecraft.getInstance();
        EntityRenderDispatcher manager = instance.getEntityRenderDispatcher();

        ItemProperties.register(ModuleTool.INFINITY_LAUNCHER.get(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "cooldown"), (stack, world, entity, number) -> {
            if (entity instanceof Player) {
                return ((Player) entity).getCooldowns().isOnCooldown(stack.getItem()) ? 1 : 2;
            }
            return 2f;
        });
        ItemProperties.register(ModuleCore.MACHINE_SETTINGS_COPIER.get(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "stored"), (stack, world, entity, number) -> {
            return stack.has(IFAttachments.SETTINGS_COPIER) ? 1 : 0;
        });
    }

}
