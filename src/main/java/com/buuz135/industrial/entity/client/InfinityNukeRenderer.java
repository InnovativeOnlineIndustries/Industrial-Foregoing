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
package com.buuz135.industrial.entity.client;

import com.buuz135.industrial.entity.InfinityNukeEntity;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class InfinityNukeRenderer extends EntityRenderer<InfinityNukeEntity> {

    public static final ResourceLocation NUKE = new ResourceLocation(Reference.MOD_ID, "textures/entity/infinity_nuke_entity.png");
    private final InfinityNukeModel nukeModel;
    private final InfinityNukeModelArmed nukeModelArmed;
    private final InfinityNukeModelArmed nukeModelArmedBig;

    public static final ModelLayerLocation NUKE_LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "infinity_nuke_entity"), "main");
    public static final ModelLayerLocation NUKE_ARMED_LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "infinity_nuke_entity"), "armed");
    public static final ModelLayerLocation NUKE_ARMED_BIG_LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "infinity_nuke_entity"), "armed_big");


    public InfinityNukeRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        nukeModel = new InfinityNukeModel(p_174008_.bakeLayer(NUKE_LAYER));
        nukeModelArmed = new InfinityNukeModelArmed(p_174008_.bakeLayer(NUKE_ARMED_LAYER));
        nukeModelArmedBig = new InfinityNukeModelArmed(p_174008_.bakeLayer(NUKE_ARMED_BIG_LAYER));
    }

    @Override
    public void render(InfinityNukeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.xRot) - 180.0F));
        matrixStackIn.translate(0, -1.35, 0.05);
        VertexConsumer ivertexbuilder = net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect(bufferIn, RenderType.entityTranslucent(this.getTextureLocation(entityIn)), false, false);
        if (entityIn.isDataArmed()) {
            if (entityIn.isDataExploding()) {
                double time = 7 + entityIn.getCommandSenderWorld().getRandom().nextInt(50);
                matrixStackIn.translate((entityIn.getCommandSenderWorld().getRandom().nextDouble() - 0.5) / time, 0, (entityIn.getCommandSenderWorld().getRandom().nextDouble() - 0.5) / time);

            }
            this.nukeModelArmed.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            if (entityIn.isDataExploding() && entityIn.level.getRandom().nextDouble() < 0.96) {
                float f = partialTicks + entityIn.getDataTicksExploding() + 10;
                ivertexbuilder = net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect(bufferIn, RenderType.energySwirl(new ResourceLocation(Reference.MOD_ID, "textures/blocks/mycelial_clean.png"), f * (entityIn.getDataTicksExploding() / 50000f), f * (entityIn.getDataTicksExploding() / 50000f)), false, false);
                //matrixStackIn.scale(1.1f,1.1f,1.1f);
                nukeModelArmedBig.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.1F);
            }
        } else {
            this.nukeModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(InfinityNukeEntity entity) {
        return NUKE;
    }


}
