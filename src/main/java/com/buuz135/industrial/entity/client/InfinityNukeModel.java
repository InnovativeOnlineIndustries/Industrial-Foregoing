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

package com.buuz135.industrial.entity.client;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.buuz135.industrial.entity.InfinityNukeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class InfinityNukeModel extends EntityModel<InfinityNukeEntity> {
    private final ModelRenderer top;
    private final ModelRenderer bottom;
    private final ModelRenderer core;
    private final ModelRenderer leg_left;
    private final ModelRenderer leg_right;
    private final ModelRenderer bone;

    public InfinityNukeModel() {
        textureWidth = 64;
        textureHeight = 64;

        top = new ModelRenderer(this);
        top.setRotationPoint(-8.0F, 17.4F, 8.0F);
        top.setTextureOffset(60, 53).addBox(10.0F, -13.4F, -11.0F, -4.0F, 3.0F, 4.0F, 0.0F, false);
        top.setTextureOffset(48, 24).addBox(6.0F, -15.4F, -11.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        top.setTextureOffset(32, 52).addBox(4.0F, -14.4F, -13.0F, 8.0F, 4.0F, 8.0F, 0.0F, false);

        bottom = new ModelRenderer(this);
        bottom.setRotationPoint(-8.0F, 17.4F, 8.0F);
        bottom.setTextureOffset(60, 53).addBox(10.0F, -4.4F, -11.0F, -4.0F, 3.0F, 4.0F, 0.0F, true);
        bottom.setTextureOffset(0, 52).addBox(4.0F, -4.4F, -13.0F, 8.0F, 4.0F, 8.0F, 0.0F, false);

        core = new ModelRenderer(this);
        core.setRotationPoint(-8.0F, 17.4F, 8.0F);
        core.setTextureOffset(50, 35).addBox(5.0F, -7.9F, -9.5F, 6.0F, 1.0F, 1.0F, 0.0F, false);
        core.setTextureOffset(52, 29).addBox(6.5F, -8.9F, -10.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);

        leg_left = new ModelRenderer(this);
        leg_left.setRotationPoint(-8.0F, 18.4F, 8.0F);
        leg_left.setTextureOffset(49, 5).addBox(11.0F, -16.4F, -10.5F, 2.0F, 16.0F, 3.0F, 0.0F, false);
        leg_left.setTextureOffset(56, 37).addBox(11.0F, 1.6F, -13.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
        leg_left.setTextureOffset(56, 43).addBox(11.0F, 1.6F, -7.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        leg_left.setTextureOffset(0, 42).addBox(11.0F, -0.4F, -13.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);

        leg_right = new ModelRenderer(this);
        leg_right.setRotationPoint(-8.0F, 17.4F, 8.0F);
        leg_right.setTextureOffset(56, 37).addBox(3.0F, 2.6F, -13.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        leg_right.setTextureOffset(0, 42).addBox(3.0F, 0.6F, -13.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
        leg_right.setTextureOffset(56, 43).addBox(3.0F, 2.6F, -7.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        leg_right.setTextureOffset(54, 5).addBox(3.0F, -15.4F, -10.5F, 2.0F, 16.0F, 3.0F, 0.0F, false);

        bone = new ModelRenderer(this);
        bone.setRotationPoint(0.0F, 24.0F, 0.0F);
        bone.setTextureOffset(42, 0).addBox(-5.0F, -26.0F, -1.5F, 10.0F, 4.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(InfinityNukeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        top.render(matrixStack, buffer, packedLight, packedOverlay);
        bottom.render(matrixStack, buffer, packedLight, packedOverlay);
        core.render(matrixStack, buffer, packedLight, packedOverlay);
        leg_left.render(matrixStack, buffer, packedLight, packedOverlay);
        leg_right.render(matrixStack, buffer, packedLight, packedOverlay);
        bone.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}