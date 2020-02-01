/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.transport.tile.ConveyorTile;
import com.buuz135.industrial.item.infinity.ItemInfinityDrill;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.module.ModuleTransport;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.event.IFClientEvents;
import com.buuz135.industrial.proxy.client.event.IFTooltipEvent;
import com.buuz135.industrial.proxy.client.event.IFWorldRenderLastEvent;
import com.buuz135.industrial.proxy.client.render.FluidConveyorTESR;
import com.buuz135.industrial.proxy.client.render.WorkingAreaTESR;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientProxy extends CommonProxy {

    public static final ResourceLocation beacon = new ResourceLocation("textures/entity/beacon_beam.png");
    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static IBakedModel ears_baked;
    public static OBJModel ears_model;

    @Override
    public void run() {
        //OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);

        MinecraftForge.EVENT_BUS.register(new IFClientEvents());
        MinecraftForge.EVENT_BUS.register(new IFWorldRenderLastEvent());
        MinecraftForge.EVENT_BUS.register(new IFTooltipEvent());

        EventManager.mod(ModelBakeEvent.class).process(event -> {
            try {
                //ears_model = OBJLoader.INSTANCE.loadModel(new ResourceLocation(Reference.MOD_ID, "models/block/catears.obj"), false, false, false, false);
                //ears_baked = ears_model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new SimpleModelState(ImmutableMap.of(), TransformationMatrix.func_227983_a_()), DefaultVertexFormats.BLOCK, ItemOverrideList.EMPTY, new ResourceLocation(Reference.MOD_ID, "models/block/catears.obj"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).subscribe();
        EventManager.mod(TextureStitchEvent.Pre.class).process(pre -> {
            pre.addSprite(new ResourceLocation(Reference.MOD_ID, "blocks/catears"));
        }).subscribe();


        ClientRegistry.bindTileEntityRenderer(ModuleTransport.CONVEYOR.getTileEntityType(), FluidConveyorTESR::new);
        BasicBlock.BLOCKS.stream().filter(blockBase -> blockBase instanceof BasicTileBlock && IndustrialAreaWorkingTile.class.isAssignableFrom(((BasicTileBlock) blockBase).getTileClass())).forEach(blockBase -> ClientRegistry.bindTileEntityRenderer(((BasicTileBlock) blockBase).getTileEntityType(), WorkingAreaTESR::new));
        //ClientRegistry.bindTileEntityRenderer(IndustrialAreaWorkingTile.class, new WorkingAreaTESR());
        //manager.entityRenderMap.put(EntityPinkSlime.class, new RenderPinkSlime(manager));

        //((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> FluidUtils.colorCache.clear());

        RenderTypeLookup.setRenderLayer(ModuleTransport.CONVEYOR, RenderType.cutout());

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn != null && pos != null) {
                TileEntity entity = worldIn.getTileEntity(pos);
                if (entity instanceof ConveyorTile) {
                    return ((ConveyorTile) entity).getColor();
                }
            }
            return 0xFFFFFFF;
        }, ModuleTransport.CONVEYOR);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 || tintIndex == 2 || tintIndex == 3) {
                SpawnEggItem info = null;
                if (stack.hasTag() && stack.getTag().contains("entity", Constants.NBT.TAG_STRING)) {
                    ResourceLocation id = new ResourceLocation(stack.getTag().getString("entity"));
                    info = SpawnEggItem.getEgg(ForgeRegistries.ENTITIES.getValue(id));
                }
                return info == null ? 0x636363 : tintIndex == 3 ? /*BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(info.spawnedID.toString())*/ false ? 0xDB201A : 0x636363 : info.getColor(tintIndex);
            }
            return 0xFFFFFF;
        }, ModuleTool.MOB_IMPRISONMENT_TOOL);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return ItemInfinityDrill.DrillTier.getTierBraquet(ModuleTool.INFINITY_DRILL.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ModuleTool.INFINITY_DRILL);
    }

}
