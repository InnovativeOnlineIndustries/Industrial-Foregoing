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
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;

public class ContributorsCatEarsRender extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    public ContributorsCatEarsRender(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CommonProxy.CONTRIBUTORS == null) return;
        if (!CommonProxy.CONTRIBUTORS.contains(entitylivingbaseIn.getUniqueID().toString())) return;
        if (!entitylivingbaseIn.isWearing(PlayerModelPart.CAPE)) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        RenderHelper.disableStandardItemLighting();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
        GlStateManager.translatef(0, -0.015f, 0);
        if (!entitylivingbaseIn.inventory.armorInventory.get(3).isEmpty()) GlStateManager.translatef(0, -0.02f, 0);
        if (entitylivingbaseIn.isSneaking()) GlStateManager.translatef(0, 0.27f, 0);
        GlStateManager.rotatef(90, 0, 1, 0);
        GlStateManager.rotatef(180, 1, 0, 0);
        GlStateManager.rotatef(netHeadYaw, 0, -1, 0);
        GlStateManager.rotatef(headPitch, 0, 0, -1);

        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
            spookyScarySkeletons();
        } else if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            itsSnowyHere();
        } else {
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(ClientProxy.ears_baked, 0.5f, 255, 255, 255);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    public void spookyScarySkeletons() {
        IBakedModel pumpkin = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(Minecraft.getInstance().world.getGameTime() % 200 < 100 ? Blocks.CARVED_PUMPKIN.getDefaultState() : Blocks.PUMPKIN.getDefaultState());
        GlStateManager.rotatef(90, 0, -1, 0);
        GlStateManager.translatef(0.08f, 0.485f, -0.1f);
        GlStateManager.scalef(2 / 16f, 3 / 16f, 2 / 16f);

        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
        GlStateManager.translatef(-0.08f * 28f, 0f, 0f);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
    }

    @OnlyIn(Dist.CLIENT)
    public void itsSnowyHere() {
        IBakedModel pumpkin = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(Blocks.TALL_GRASS.getDefaultState());
        GlStateManager.rotatef(90, 0, -1, 0);
        GlStateManager.translatef(0.08f, 0.485f, -0.1f);
        GlStateManager.scalef(2 / 16f, 2 / 16f, 2 / 16f);

        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
        GlStateManager.translatef(-0.08f * 28, 0, 0);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(pumpkin, 0.5f, 255, 255, 255);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
