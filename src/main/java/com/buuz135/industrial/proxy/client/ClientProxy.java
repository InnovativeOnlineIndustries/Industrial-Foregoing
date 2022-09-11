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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.transportstorage.tile.BHTile;
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleTankTile;
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
import com.buuz135.industrial.proxy.network.BackpackOpenMessage;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Calendar;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static BakedModel ears_baked;

    public KeyMapping OPEN_BACKPACK;

    @Override
    public void run() {
        OPEN_BACKPACK = new KeyMapping("key.industrialforegoing.backpack.desc", -1, "key.industrialforegoing.category");
        EventManager.forge(RegisterKeyMappingsEvent.class).process(event -> {
            event.register(OPEN_BACKPACK);
        }).subscribe();
        EventManager.forge(TickEvent.ClientTickEvent.class).process(event -> {
            if (OPEN_BACKPACK.consumeClick()) {
                IndustrialForegoing.NETWORK.get().sendToServer(new BackpackOpenMessage(Screen.hasControlDown()));
            }
        }).subscribe();


        MinecraftForge.EVENT_BUS.register(new IFClientEvents());

        //((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> FluidUtils.colorCache.clear());

        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.CONVEYOR.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getLeft().get(), RenderType.cutout());

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null) {
                BlockEntity entity = worldIn.getBlockEntity(pos);
                if (entity instanceof ConveyorTile) {
                    return ((ConveyorTile) entity).getColor();
                }
            }
            return 0xFFFFFFF;
        }, ModuleTransportStorage.CONVEYOR.getLeft().get());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 || tintIndex == 2 || tintIndex == 3) {
                SpawnEggItem info = null;
                if (stack.hasTag() && stack.getTag().contains("entity")) {
                    ResourceLocation id = new ResourceLocation(stack.getTag().getString("entity"));
                    info = SpawnEggItem.byId(ForgeRegistries.ENTITY_TYPES.getValue(id));
                }
                return info == null ? 0x636363 : tintIndex == 3 ? ((MobImprisonmentToolItem) ModuleTool.MOB_IMPRISONMENT_TOOL.get()).isBlacklisted(info.getType(new CompoundTag())) ? 0xDB201A : 0x636363 : info.getColor(tintIndex - 1);
            }
            return 0xFFFFFF;
        }, ModuleTool.MOB_IMPRISONMENT_TOOL.get());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_BACKPACK.get(), ModuleTool.INFINITY_LAUNCHER.get(), ModuleTool.INFINITY_NUKE.get(), ModuleTool.INFINITY_TRIDENT.get(), ModuleTool.INFINITY_HAMMER.get(), ModuleTool.INFINITY_SAW.get(), ModuleTool.INFINITY_DRILL.get());
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null && worldIn.getBlockEntity(pos) instanceof BlackHoleTankTile) {
                BlackHoleTankTile tank = (BlackHoleTankTile) worldIn.getBlockEntity(pos);
                if (tank != null && tank.getTank().getFluidAmount() > 0) {
                    int color = FluidUtils.getFluidColor(tank.getTank().getFluid());
                    if (color != -1) return color;
                }
            }
            return 0xFFFFFF;
        }, ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getLeft().get());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElseGet(null);
                if (fluidHandlerItem.getFluidInTank(0).getAmount() > 0) {
                    int color = FluidUtils.getFluidColor(fluidHandlerItem.getFluidInTank(0));
                    if (color != -1) return color;
                }
            }
            return 0xFFFFFF;
        }, ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getLeft().get(), ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getLeft().get());
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElseGet(null);
                if (fluidHandlerItem.getFluidInTank(0).getAmount() > 0) {
                    int color = FluidUtils.getFluidColor(fluidHandlerItem.getFluidInTank(0));
                    if (color != -1) return color;
                }
            }
            return 0xFFFFFF;
        }, ModuleCore.RAW_ORE_MEAT.getBucketFluid(), ModuleCore.FERMENTED_ORE_MEAT.getBucketFluid());

        EventManager.forge(ItemTooltipEvent.class).filter(event -> ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).getNamespace().equals(Reference.MOD_ID)).process(event -> {
            if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL) {
                event.getToolTip().add(Component.literal("Press Alt + F4 to cheat this item").withStyle(ChatFormatting.DARK_AQUA));
            }
        }).subscribe();

        Minecraft instance = Minecraft.getInstance();
        EntityRenderDispatcher manager = instance.getEntityRenderDispatcher();

        ItemProperties.register(ModuleTool.INFINITY_LAUNCHER.get(), new ResourceLocation(Reference.MOD_ID, "cooldown"), (stack, world, entity, number) -> {
            if (entity instanceof Player) {
                return ((Player) entity).getCooldowns().isOnCooldown(stack.getItem()) ? 1 : 2;
            }
            return 2f;
        });
    }

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

        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_UNIT_PITY.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_UNIT_SIMPLE.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_UNIT_ADVANCED.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_UNIT_SUPREME.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getRight().get(), BlackHoleUnitTESR::new);
        event.registerBlockEntityRenderer((BlockEntityType<? extends BHTile>) ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getRight().get(), BlackHoleUnitTESR::new);

        event.registerBlockEntityRenderer((BlockEntityType<? extends MycelialReactorTile>) ModuleGenerator.MYCELIAL_REACTOR.getRight().get(), MycelialReactorTESR::new);

        event.registerEntityRenderer((EntityType<? extends InfinityTridentEntity>) ModuleTool.TRIDENT_ENTITY_TYPE.get(), InfinityTridentRenderer::new);
        event.registerEntityRenderer((EntityType<? extends InfinityNukeEntity>) ModuleTool.INFINITY_NUKE_ENTITY_TYPE.get(), InfinityNukeRenderer::new);
        event.registerEntityRenderer((EntityType<? extends InfinityLauncherProjectileEntity>) ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE.get(), InfinityLauncherProjectileRenderer::new);

        event.registerBlockEntityRenderer(((BlockEntityType<? extends TransporterTile>) ModuleTransportStorage.TRANSPORTER.getRight().get()), TransporterTESR::new);

        event.registerBlockEntityRenderer(((BlockEntityType<? extends ConveyorTile>) ModuleTransportStorage.CONVEYOR.getRight().get()), FluidConveyorTESR::new);
    }

    private static void registerAreaRender(EntityRenderersEvent.RegisterRenderers event, Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> pair) {
        event.registerBlockEntityRenderer((BlockEntityType<? extends IndustrialAreaWorkingTile>) pair.getRight().get(), WorkingAreaTESR::new);
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(InfinityTridentRenderer.TRIDENT_LAYER, InfinityTridentModel::createBodyLayer);
        event.registerLayerDefinition(InfinityNukeRenderer.NUKE_LAYER, InfinityNukeModel::createBodyLayer);
        event.registerLayerDefinition(InfinityNukeRenderer.NUKE_ARMED_LAYER, () -> InfinityNukeModelArmed.createBodyLayer(new CubeDeformation(0f)));
        event.registerLayerDefinition(InfinityNukeRenderer.NUKE_ARMED_BIG_LAYER, () -> InfinityNukeModelArmed.createBodyLayer(new CubeDeformation(0.2f)));
        event.registerLayerDefinition(InfinityLauncherProjectileRenderer.PROJECTILE_LAYER, InfinityLauncherProjectileModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            renderer.addLayer(new ContributorsCatEarsRender(renderer));
            renderer.addLayer(new InfinityLauncherProjectileArmorLayer(renderer));
        }
    }

}
