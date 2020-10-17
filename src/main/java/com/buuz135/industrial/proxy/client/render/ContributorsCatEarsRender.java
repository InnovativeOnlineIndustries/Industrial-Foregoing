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

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

public class ContributorsCatEarsRender extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    public ContributorsCatEarsRender(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int p_225628_3_, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().containsKey(entitylivingbaseIn.getUniqueID())) return;
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().get(entitylivingbaseIn.getUniqueID()).getEnabled().containsKey(new ResourceLocation(Reference.MOD_ID, "cat_ears")))
            return;
        stack.push();
        stack.translate(0, -0.015f, 0);
        if (!entitylivingbaseIn.inventory.armorInventory.get(3).isEmpty()) stack.translate(0, -0.02f, 0);
        if (entitylivingbaseIn.isCrouching()) stack.translate(0, 0.27f, 0);
        stack.rotate(Vector3f.YP.rotationDegrees(90));
        stack.rotate(Vector3f.XP.rotationDegrees(180));
        stack.rotate(Vector3f.YN.rotationDegrees(netHeadYaw));
        stack.rotate(Vector3f.ZN.rotationDegrees(headPitch));
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
            spookyScarySkeletons(stack, buffer);
        } else if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            itsSnowyHere(stack, buffer);
        } else {
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(stack.getLast(), buffer.getBuffer(RenderType.getCutout()), null, ClientProxy.ears_baked, 1f, 1f, 1f, p_225628_3_, OverlayTexture.NO_OVERLAY);
        }
        stack.pop();
    }

    @OnlyIn(Dist.CLIENT)
    public void spookyScarySkeletons(MatrixStack stack, IRenderTypeBuffer buffer) {
        IBakedModel pumpkin = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(Minecraft.getInstance().world.getGameTime() % 200 < 100 ? Blocks.CARVED_PUMPKIN.getDefaultState() : Blocks.JACK_O_LANTERN.getDefaultState());
        stack.rotate(Vector3f.YN.rotationDegrees(90f));
        stack.translate(0.08f, 0.485f, -0.1f);
        stack.scale(2 / 16f, 2 / 16f, 2 / 16f);

        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(stack.getLast(), buffer.getBuffer(RenderType.getSolid()), null, pumpkin, 0.5f, 255, 255, 255, 255);
        stack.translate(-0.08f * 28f, 0f, 0f);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(stack.getLast(), buffer.getBuffer(RenderType.getSolid()), null, pumpkin, 0.5f, 255, 255, 255, 255);
    }

    @OnlyIn(Dist.CLIENT)
    public void itsSnowyHere(MatrixStack stack, IRenderTypeBuffer buffer) {
        IBakedModel pumpkin = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(Blocks.CAKE.getDefaultState());
        stack.rotate(Vector3f.YN.rotationDegrees(90f));
        stack.translate(0.08f, 0.485f, -0.1f);
        stack.scale(2 / 16f, 2 / 16f, 2 / 16f);

        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(stack.getLast(), buffer.getBuffer(RenderType.getSolid()), null, pumpkin, 255, 255, 255, 255, 255);
        stack.translate(-0.08f * 28f, 0f, 0f);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(stack.getLast(), buffer.getBuffer(RenderType.getSolid()), null, pumpkin, 255, 255, 255, 255, 255);
    }

}
