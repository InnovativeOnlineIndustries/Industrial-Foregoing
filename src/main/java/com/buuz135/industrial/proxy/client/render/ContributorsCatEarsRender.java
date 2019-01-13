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
package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;

public class ContributorsCatEarsRender implements LayerRenderer<AbstractClientPlayer> {

    @SideOnly(Side.CLIENT)
    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CommonProxy.CONTRIBUTORS == null) return;
        if (!CommonProxy.CONTRIBUTORS.contains(entitylivingbaseIn.getUniqueID().toString())) return;
        if (!entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        RenderHelper.disableStandardItemLighting();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
        GlStateManager.translate(0, -0.015f, 0);
        if (!entitylivingbaseIn.inventory.armorInventory.get(3).isEmpty()) GlStateManager.translate(0, -0.02f, 0);
        if (entitylivingbaseIn.isSneaking()) GlStateManager.translate(0, 0.27, 0);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(netHeadYaw, 0, -1, 0);
        GlStateManager.rotate(headPitch, 0, 0, -1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
            spookyScarySkeletons();
        } else if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            itsSnowyHere();
        } else {
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(ClientProxy.ears_baked, 0.5f, 255, 255, 255);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void spookyScarySkeletons() {
        IBakedModel pumpkin = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Minecraft.getMinecraft().world.getTotalWorldTime() % 200 < 100 ? Blocks.LIT_PUMPKIN.getDefaultState() : Blocks.PUMPKIN.getDefaultState());
        GlStateManager.rotate(90, 0, -1, 0);
        GlStateManager.translate(0.08, 0.485, -0.1);
        GlStateManager.scale(2 / 16D, 3 / 16D, 2 / 16D);

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
        GlStateManager.translate(-0.08 * 28, 0, 0);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
    }

    public void itsSnowyHere() {
        IBakedModel pumpkin = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.TALLGRASS.getStateFromMeta(2));
        GlStateManager.rotate(90, 0, -1, 0);
        GlStateManager.translate(0.08, 0.485, -0.1);
        GlStateManager.scale(2 / 16D, 2 / 16D, 2 / 16D);

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
        GlStateManager.translate(-0.08 * 28, 0, 0);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
