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

import com.buuz135.industrial.entity.InfinityTridentEntity;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

public class InfinityTridentRenderer extends EntityRenderer<InfinityTridentEntity> {
    protected InfinityTridentRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(InfinityTridentEntity p_114482_) {
        return null;
    }

//    public static final ResourceLocation TRIDENT = new ResourceLocation(Reference.MOD_ID, "textures/items/infinity_trident.png");
//    private final InfinityTridentModel tridentModel = new InfinityTridentModel();
//
//    public InfinityTridentRenderer(EntityRenderDispatcher renderManagerIn) {
//        super(renderManagerIn);
//    }
//
//    @Override
//    public void render(InfinityTridentEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
//        matrixStackIn.pushPose();
//        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90.0F));
//        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.xRot) + 90.0F));
//        matrixStackIn.translate(0.5, -0.5, -0.5);
//        VertexConsumer ivertexbuilder = net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect(bufferIn, RenderType.entityTranslucent(this.getTextureLocation(entityIn)), false, entityIn.isEnchanted());
//        this.tridentModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//        matrixStackIn.popPose();
//        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//    }
//
//    @Override
//    public ResourceLocation getTextureLocation(InfinityTridentEntity entity) {
//        return TRIDENT;
//    }
}
