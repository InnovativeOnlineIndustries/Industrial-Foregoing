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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class InfinityNukeRenderer extends EntityRenderer<InfinityNukeEntity> {

    public static final ResourceLocation NUKE = new ResourceLocation(Reference.MOD_ID, "textures/entity/infinity_nuke_entity.png");
    private final InfinityNukeModel nukeModel = new InfinityNukeModel();
    private final InfinityNukeModelArmed nukeModelArmed = new InfinityNukeModelArmed(0);
    private final InfinityNukeModelArmed nukeModelArmedBig = new InfinityNukeModelArmed(0.2f);


    public InfinityNukeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(InfinityNukeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch) - 180.0F));
        matrixStackIn.translate(0, -1.35, 0.05);
        IVertexBuilder ivertexbuilder = net.minecraft.client.renderer.ItemRenderer.getEntityGlintVertexBuilder(bufferIn, RenderType.getEntityTranslucent(this.getEntityTexture(entityIn)), false, false);
        if (entityIn.isDataArmed()) {
            if (entityIn.isDataExploding()) {
                double time = 7 + entityIn.getEntityWorld().getRandom().nextInt(50);
                matrixStackIn.translate((entityIn.getEntityWorld().getRandom().nextDouble() - 0.5) / time, 0, (entityIn.getEntityWorld().getRandom().nextDouble() - 0.5) / time);

            }
            this.nukeModelArmed.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            if (entityIn.isDataExploding() && entityIn.world.getRandom().nextDouble() < 0.96) {
                float f = partialTicks + entityIn.getDataTicksExploding() + 10;
                ivertexbuilder = net.minecraft.client.renderer.ItemRenderer.getEntityGlintVertexBuilder(bufferIn, RenderType.getEnergySwirl(new ResourceLocation(Reference.MOD_ID, "textures/blocks/mycelial_clean.png"), f * (entityIn.getDataTicksExploding() / 50000f), f * (entityIn.getDataTicksExploding() / 50000f)), false, false);
                //matrixStackIn.scale(1.1f,1.1f,1.1f);
                nukeModelArmedBig.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.1F);
            }
        } else {
            this.nukeModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(InfinityNukeEntity entity) {
        return NUKE;
    }


}
