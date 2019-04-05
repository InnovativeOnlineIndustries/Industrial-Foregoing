/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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

import com.buuz135.industrial.entity.EntityPinkSlime;
import com.buuz135.industrial.item.infinity.ItemInfinityDrill;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import com.buuz135.industrial.proxy.client.entity.RenderPinkSlime;
import com.buuz135.industrial.proxy.client.event.IFClientEvents;
import com.buuz135.industrial.proxy.client.event.IFTooltipEvent;
import com.buuz135.industrial.proxy.client.event.IFWorldRenderLastEvent;
import com.buuz135.industrial.proxy.client.render.ContributorsCatEarsRender;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    public static final ResourceLocation beacon = new ResourceLocation("textures/entity/beacon_beam.png");
    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static IBakedModel ears_baked;
    public static IModel ears_model;

    @Override
    public void run() {
        super.run();

        OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);

        MinecraftForge.EVENT_BUS.register(new IFClientEvents());
        MinecraftForge.EVENT_BUS.register(new IFWorldRenderLastEvent());
        MinecraftForge.EVENT_BUS.register(new IFTooltipEvent());

        try {
            ears_model = OBJLoader.INSTANCE.loadModel(new ResourceLocation(Reference.MOD_ID, "models/block/catears.obj"));
            ears_baked = ears_model.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), TRSRTransformation.identity(), false, DefaultVertexFormats.BLOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RenderManager manager = Minecraft.getInstance().getRenderManager();
        Map<String, RenderPlayer> map = manager.getSkinMap();
        map.get("default").addLayer(new ContributorsCatEarsRender());
        map.get("slim").addLayer(new ContributorsCatEarsRender());

        manager.entityRenderMap.put(EntityPinkSlime.class, new RenderPinkSlime(manager));

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> FluidUtils.colorCache.clear());

        if (ItemRegistry.dyes != null)
            Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> ItemDye.DYE_COLORS[EnumDyeColor.byMetadata(stack.getMetadata()).getDyeDamage()], ItemRegistry.artificalDye);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ItemDye.DYE_COLORS[EnumDyeColor.byMetadata(stack.getMetadata()).getDyeDamage()];
            }
            return 0xFFFFFF;
        }, BlockRegistry.blockConveyor.getItem());
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0) {
                TileEntity entity = worldIn.getTileEntity(pos);
                if (entity instanceof TileEntityConveyor) {
                    return ItemDye.DYE_COLORS[((TileEntityConveyor) entity).getColor()];
                }
            }
            return 0xFFFFFFF;
        }, BlockRegistry.blockConveyor);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1 || tintIndex == 2 || tintIndex == 3) {
                EntityList.EntityEggInfo info = null;
                if (stack.hasTag() && stack.getTag().hasKey("entity", Constants.NBT.TAG_STRING)) {
                    ResourceLocation id = new ResourceLocation(stack.getTag().getString("entity"));
                    info = EntityList.ENTITY_EGGS.get(id);
                }
                return info == null ? 0x636363 : tintIndex == 3 ? BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(info.spawnedID.toString()) ? 0xDB201A : 0x636363 : tintIndex == 1 ? info.primaryColor : info.secondaryColor;
            }
            return 0xFFFFFF;
        }, ItemRegistry.mobImprisonmentToolItem);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return ItemInfinityDrill.DrillTier.getTierBraquet(ItemRegistry.itemInfinityDrill.getPowerFromStack(stack)).getLeft().getTextureColor();
            }
            return 0xFFFFFF;
        }, ItemRegistry.itemInfinityDrill);
    }

}
