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

package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

public class ContributorsCatEarsRender extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public ContributorsCatEarsRender(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int p_225628_3_, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().containsKey(entitylivingbaseIn.getUUID())) return;
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().get(entitylivingbaseIn.getUUID()).getEnabled().containsKey(new ResourceLocation(Reference.MOD_ID, "cat_ears")))
            return;
        stack.pushPose();
        stack.translate(0, -0.015f, 0);
        if (!entitylivingbaseIn.inventory.armor.get(3).isEmpty()) stack.translate(0, -0.02f, 0);
        if (entitylivingbaseIn.isCrouching()) stack.translate(0, 0.27f, 0);
        String type = ClientRewardStorage.REWARD_STORAGE.getRewards().get(entitylivingbaseIn.getUUID()).getEnabled().get(new ResourceLocation(Reference.MOD_ID, "cat_ears"));
        stack.mulPose(Axis.YP.rotationDegrees(90));
        stack.mulPose(Axis.XP.rotationDegrees(180));
        stack.mulPose(Axis.YN.rotationDegrees(netHeadYaw));
        stack.mulPose(Axis.ZN.rotationDegrees(headPitch));
        Calendar calendar = Calendar.getInstance();
        if ((calendar.get(Calendar.MONTH) == Calendar.OCTOBER && type.equalsIgnoreCase("normal")) || type.equalsIgnoreCase("spooky")) {
            spookyScarySkeletons(stack, buffer);
        } else if ((calendar.get(Calendar.MONTH) == Calendar.DECEMBER && type.equalsIgnoreCase("normal")) || type.equalsIgnoreCase("snowy")) {
            itsSnowyHere(stack, buffer);
        } else if (ClientProxy.ears_baked != null) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), buffer.getBuffer(RenderType.cutout()), null, ClientProxy.ears_baked, 1f, 1f, 1f, p_225628_3_, OverlayTexture.NO_OVERLAY);
        }
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void spookyScarySkeletons(PoseStack stack, MultiBufferSource buffer) {
        BakedModel pumpkin = Minecraft.getInstance().getBlockRenderer().getBlockModel(Minecraft.getInstance().level.getGameTime() % 200 < 100 ? Blocks.CARVED_PUMPKIN.defaultBlockState() : Blocks.JACK_O_LANTERN.defaultBlockState());
        stack.mulPose(Axis.YN.rotationDegrees(90f));
        stack.translate(0.08f, 0.485f, -0.1f);
        stack.scale(2 / 16f, 2 / 16f, 2 / 16f);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), buffer.getBuffer(RenderType.solid()), null, pumpkin, 0.5f, 255, 255, 255, 255);
        stack.translate(-0.08f * 28f, 0f, 0f);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), buffer.getBuffer(RenderType.solid()), null, pumpkin, 0.5f, 255, 255, 255, 255);
    }

    @OnlyIn(Dist.CLIENT)
    public void itsSnowyHere(PoseStack stack, MultiBufferSource buffer) {
        BakedModel pumpkin = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.CAKE.defaultBlockState());
        stack.mulPose(Axis.YN.rotationDegrees(90f));
        stack.translate(0.08f, 0.485f, -0.1f);
        stack.scale(2 / 16f, 2 / 16f, 2 / 16f);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), buffer.getBuffer(RenderType.solid()), null, pumpkin, 255, 255, 255, 255, 255);
        stack.translate(-0.08f * 28f, 0f, 0f);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), buffer.getBuffer(RenderType.solid()), null, pumpkin, 255, 255, 255, 255, 255);
    }

}
