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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleTankTile;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.event.IFClientEvents;
import com.buuz135.industrial.proxy.client.render.BlackHoleUnitTESR;
import com.buuz135.industrial.proxy.client.render.ContributorsCatEarsRender;
import com.buuz135.industrial.proxy.client.render.MycelialReactorTESR;
import com.buuz135.industrial.proxy.client.render.WorkingAreaTESR;
import com.buuz135.industrial.proxy.network.BackpackOpenMessage;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.TitaniumClient;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.reward.Reward;
import com.hrznstudio.titanium.reward.RewardGiver;
import com.hrznstudio.titanium.reward.RewardManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    public static final ResourceLocation beacon = new ResourceLocation("textures/entity/beacon_beam.png");
    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static BakedModel ears_baked;
    public static OBJModel ears_model;

    public static final SoundEvent NUKE_EXPLOSION = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "nuke_explosion")).setRegistryName(new ResourceLocation(Reference.MOD_ID, "nuke_explosion"));
    public static final SoundEvent NUKE_ARMING = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "nuke_arming")).setRegistryName(new ResourceLocation(Reference.MOD_ID, "nuke_arming"));

    public KeyMapping OPEN_BACKPACK;

    @Override
    public void run() {
        OPEN_BACKPACK = new KeyMapping("key.industrialforegoing.backpack.desc", -1, "key.industrialforegoing.category");
        ClientRegistry.registerKeyBinding(OPEN_BACKPACK);
        EventManager.forge(TickEvent.ClientTickEvent.class).process(event -> {
            if (OPEN_BACKPACK.consumeClick()) {
                IndustrialForegoing.NETWORK.get().sendToServer(new BackpackOpenMessage(Screen.hasControlDown()));
            }
        }).subscribe();


        MinecraftForge.EVENT_BUS.register(new IFClientEvents());

        EventManager.mod(ModelBakeEvent.class).process(event -> {
            ears_baked = event.getModelRegistry().get(new ResourceLocation(Reference.MOD_ID, "block/catears"));
        }).subscribe();
        EventManager.forgeGeneric(RegistryEvent.Register.class, SoundEvent.class).process(registryEvent -> ((RegistryEvent.Register) registryEvent).getRegistry().registerAll(NUKE_ARMING, NUKE_EXPLOSION)).subscribe();

        // TODO: 22/08/2021 A lot of render changes.
//        manager.entityRenderMap.put(EntityPinkSlime.class, new RenderPinkSlime(manager));

        //((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> FluidUtils.colorCache.clear());

//        ClientRegistry.bindTileEntityRenderer(ModuleGenerator.MYCELIAL_REACTOR.getTileEntityType(), MycelialReactorTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.CONVEYOR.getTileEntityType(), FluidConveyorTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_PITY.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_SIMPLE.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_ADVANCED.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_SUPREME.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getTileEntityType(), BlackHoleUnitTESR::new);
//        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.TRANSPORTER.getTileEntityType(), TransporterTESR::new);

        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.CONVEYOR, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_PITY, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModuleCore.DARK_GLASS, RenderType.translucent());

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null) {
                BlockEntity entity = worldIn.getBlockEntity(pos);
                if (entity instanceof ConveyorTile) {
                    return ((ConveyorTile) entity).getColor();
                }
            }
            return 0xFFFFFFF;
        }, ModuleTransportStorage.CONVEYOR);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 || tintIndex == 2 || tintIndex == 3) {
                SpawnEggItem info = null;
                if (stack.hasTag() && stack.getTag().contains("entity", Constants.NBT.TAG_STRING)) {
                    ResourceLocation id = new ResourceLocation(stack.getTag().getString("entity"));
                    info = SpawnEggItem.byId(ForgeRegistries.ENTITIES.getValue(id));
                }
                return info == null ? 0x636363 : tintIndex == 3 ? ModuleTool.MOB_IMPRISONMENT_TOOL.isBlacklisted(info.getType(new CompoundTag())) ? 0xDB201A : 0x636363 : info.getColor(tintIndex - 1);
            }
            return 0xFFFFFF;
        }, ModuleTool.MOB_IMPRISONMENT_TOOL);
//        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
//            if (tintIndex == 0) {
//                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
//            }
//            return 0xFFFFFF;
//        }, ModuleTool.INFINITY_BACKPACK, ModuleTool.INFINITY_LAUNCHER, ModuleTool.INFINITY_NUKE, ModuleTool.INFINITY_TRIDENT, ModuleTool.INFINITY_HAMMER, ModuleTool.INFINITY_SAW, ModuleTool.INFINITY_DRILL);
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null && worldIn.getBlockEntity(pos) instanceof BlackHoleTankTile) {
                BlackHoleTankTile tank = (BlackHoleTankTile) worldIn.getBlockEntity(pos);
                if (tank != null && tank.getTank().getFluidAmount() > 0) {
                    int color = FluidUtils.getFluidColor(tank.getTank().getFluid());
                    if (color != -1) return color;
                }
            }
            return 0xFFFFFF;
        }, ModuleTransportStorage.BLACK_HOLE_TANK_COMMON, ModuleTransportStorage.BLACK_HOLE_TANK_PITY, ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE, ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED, ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseGet(null);
                if (fluidHandlerItem.getFluidInTank(0).getAmount() > 0){
                    int color = FluidUtils.getFluidColor(fluidHandlerItem.getFluidInTank(0));
                    if (color != -1) return color;
                }
            }
            return 0xFFFFFF;
        },ModuleTransportStorage.BLACK_HOLE_TANK_COMMON, ModuleTransportStorage.BLACK_HOLE_TANK_PITY, ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE, ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED, ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseGet(null);
                if (fluidHandlerItem.getFluidInTank(0).getAmount() > 0){
                    int color = FluidUtils.getFluidColor(fluidHandlerItem.getFluidInTank(0));
                    if (color != -1) return color;
                }
            }
            return 0xFFFFFF;
        }, ModuleCore.RAW_ORE_MEAT.getBucketFluid(), ModuleCore.FERMENTED_ORE_MEAT.getBucketFluid());

//        RenderingRegistry.registerEntityRenderingHandler(ModuleTool.TRIDENT_ENTITY_TYPE, InfinityTridentRenderer::new);
//        RenderingRegistry.registerEntityRenderingHandler(ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE, InfinityLauncherProjectileRenderer::new);
//        RenderingRegistry.registerEntityRenderingHandler(ModuleTool.INFINITY_NUKE_ENTITY_TYPE, InfinityNukeRenderer::new);

        EventManager.forge(ItemTooltipEvent.class).filter(event -> event.getItemStack().getItem().getRegistryName().getNamespace().equals(Reference.MOD_ID)).process(event -> {
            if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL) {
                event.getToolTip().add(new TextComponent("Press Alt + F4 to cheat this item").withStyle(ChatFormatting.DARK_AQUA));
            }
        }).subscribe();

        Minecraft instance = Minecraft.getInstance();
        EntityRenderDispatcher manager = instance.getEntityRenderDispatcher();
//        manager.getSkinMap().get("default").addLayer(new InfinityLauncherProjectileArmorLayer<>(TitaniumClient.getPlayerRenderer(Minecraft.getInstance())));
//        manager.getSkinMap().get("slim").addLayer(new InfinityLauncherProjectileArmorLayer(TitaniumClient.getPlayerRenderer(Minecraft.getInstance())));
//        ItemProperties.register(ModuleTool.INFINITY_LAUNCHER, new ResourceLocation(Reference.MOD_ID, "cooldown"), (stack, world, entity) -> {
//            if (entity instanceof Player) {
//                return ((Player) entity).getCooldowns().isOnCooldown(stack.getItem()) ? 1 : 2;
//            }
//            return 2;
//        });
    }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            NonNullLazy<List<Block>> blocksToProcess = NonNullLazy.of(() ->
                    ForgeRegistries.BLOCKS.getValues()
                            .stream()
                            .filter(basicBlock -> Optional.ofNullable(basicBlock.getRegistryName())
                                    .map(ResourceLocation::getNamespace)
                                    .filter(Reference.MOD_ID::equalsIgnoreCase)
                                    .isPresent())
                            .collect(Collectors.toList())
            );
            blocksToProcess.get().stream().filter(blockBase -> blockBase instanceof BasicTileBlock && IndustrialAreaWorkingTile.class.isAssignableFrom(((BasicTileBlock) blockBase).getTileClass())).forEach(blockBase -> event.registerBlockEntityRenderer(((BasicTileBlock) blockBase).getTileEntityType(), WorkingAreaTESR::new));


            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_PITY.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_SIMPLE.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_ADVANCED.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_SUPREME.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
            event.registerBlockEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getTileEntityType(), BlackHoleUnitTESR::new);

            event.registerBlockEntityRenderer(ModuleGenerator.MYCELIAL_REACTOR.getTileEntityType(), MycelialReactorTESR::new);
        }

}
