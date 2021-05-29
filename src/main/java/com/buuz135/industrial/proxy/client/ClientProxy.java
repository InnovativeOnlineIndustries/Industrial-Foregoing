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
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleTankTile;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.entity.client.InfinityTridentRenderer;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.event.IFClientEvents;
import com.buuz135.industrial.proxy.client.render.*;
import com.buuz135.industrial.proxy.network.BackpackOpenMessage;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientProxy extends CommonProxy {

    public static final ResourceLocation beacon = new ResourceLocation("textures/entity/beacon_beam.png");
    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static IBakedModel ears_baked;
    public static OBJModel ears_model;

    public KeyBinding OPEN_BACKPACK;

    @Override
    public void run() {
        OPEN_BACKPACK = new KeyBinding("key.industrialforegoing.backpack.desc", -1, "key.industrialforegoing.category");
        ClientRegistry.registerKeyBinding(OPEN_BACKPACK);
        EventManager.forge(TickEvent.ClientTickEvent.class).process(event -> {
            if (OPEN_BACKPACK.isPressed()){
                IndustrialForegoing.NETWORK.get().sendToServer(new BackpackOpenMessage(Screen.hasControlDown()));
            }
        }).subscribe();


        MinecraftForge.EVENT_BUS.register(new IFClientEvents());

        EventManager.mod(ModelBakeEvent.class).process(event -> {
            ears_baked = event.getModelRegistry().get(new ResourceLocation(Reference.MOD_ID, "block/catears"));
        }).subscribe();

        NonNullLazy<List<Block>> blocksToProcess = NonNullLazy.of(() ->
                ForgeRegistries.BLOCKS.getValues()
                        .stream()
                        .filter(basicBlock -> Optional.ofNullable(basicBlock.getRegistryName())
                                .map(ResourceLocation::getNamespace)
                                .filter(Reference.MOD_ID::equalsIgnoreCase)
                                .isPresent())
                        .collect(Collectors.toList())
        );
        blocksToProcess.get().stream().filter(blockBase -> blockBase instanceof BasicTileBlock && IndustrialAreaWorkingTile.class.isAssignableFrom(((BasicTileBlock) blockBase).getTileClass())).forEach(blockBase -> ClientRegistry.bindTileEntityRenderer(((BasicTileBlock) blockBase).getTileEntityType(), WorkingAreaTESR::new));
        //ClientRegistry.bindTileEntityRenderer(IndustrialAreaWorkingTile.class, new WorkingAreaTESR());
        //manager.entityRenderMap.put(EntityPinkSlime.class, new RenderPinkSlime(manager));

        //((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> FluidUtils.colorCache.clear());

        ClientRegistry.bindTileEntityRenderer(ModuleGenerator.MYCELIAL_REACTOR.getTileEntityType(), MycelialReactorTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.CONVEYOR.getTileEntityType(), FluidConveyorTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_PITY.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_SIMPLE.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_ADVANCED.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_UNIT_SUPREME.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_PITY.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME.getTileEntityType(), BlackHoleUnitTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModuleTransportStorage.TRANSPORTER.getTileEntityType(), TransporterTESR::new);

        RenderTypeLookup.setRenderLayer(ModuleTransportStorage.CONVEYOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_PITY, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_SIMPLE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_ADVANCED, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModuleTransportStorage.BLACK_HOLE_TANK_SUPREME, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModuleCore.DARK_GLASS, RenderType.getTranslucent());

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null) {
                TileEntity entity = worldIn.getTileEntity(pos);
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
                    info = SpawnEggItem.getEgg(ForgeRegistries.ENTITIES.getValue(id));
                }
                return info == null ? 0x636363 : tintIndex == 3 ? ModuleTool.MOB_IMPRISONMENT_TOOL.isBlacklisted(info.getType(new CompoundNBT())) ? 0xDB201A : 0x636363 : info.getColor(tintIndex - 1);
            }
            return 0xFFFFFF;
        }, ModuleTool.MOB_IMPRISONMENT_TOOL);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_DRILL);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_SAW);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_HAMMER);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_TRIDENT);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return InfinityTier.getTierBraquet(ItemInfinity.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_BACKPACK);
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null && worldIn.getTileEntity(pos) instanceof BlackHoleTankTile) {
                BlackHoleTankTile tank = (BlackHoleTankTile) worldIn.getTileEntity(pos);
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

        RenderingRegistry.registerEntityRenderingHandler(ModuleTool.TRIDENT_ENTITY_TYPE, InfinityTridentRenderer::new);

        EventManager.forge(ItemTooltipEvent.class).filter(event -> event.getItemStack().getItem().getRegistryName().getNamespace().equals(Reference.MOD_ID)).process(event -> {
            if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL){
                event.getToolTip().add(new StringTextComponent("Press Alt + F4 to cheat this item").mergeStyle(TextFormatting.DARK_AQUA));
            }
        }).subscribe();
    }

}
