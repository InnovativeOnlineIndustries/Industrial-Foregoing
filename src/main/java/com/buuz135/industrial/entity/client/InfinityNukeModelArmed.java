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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public class InfinityNukeModelArmed extends EntityModel<InfinityNukeEntity> {
    @Override
    public void setupAnim(InfinityNukeEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }

    @Override
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {

    }
//    private final ModelPart top;
//    private final ModelPart bottom;
//    private final ModelPart core;
//    private final ModelPart leg_left;
//    private final ModelPart leg_right;
//    private final ModelPart bone;
//
//    public InfinityNukeModelArmed(float size) {
//        texWidth = 64;
//        texHeight = 64;
//
//        top = new ModelPart(this);
//        top.setPos(-8.0F, 17.4F, 8.0F);
//        top.texOffs(60, 53).addBox(10.0F, -10.4F, -11.0F, -4.0F, 3.0F, 4.0F, size, false);
//        top.texOffs(48, 24).addBox(6.0F, -12.4F, -11.0F, 4.0F, 1.0F, 4.0F, size, false);
//        top.texOffs(32, 52).addBox(4.0F, -11.4F, -13.0F, 8.0F, 4.0F, 8.0F, size, false);
//
//        bottom = new ModelPart(this);
//        bottom.setPos(-8.0F, 17.4F, 8.0F);
//        bottom.texOffs(60, 53).addBox(10.0F, -7.4F, -11.0F, -4.0F, 3.0F, 4.0F, size, true);
//        bottom.texOffs(0, 52).addBox(4.0F, -7.4F, -13.0F, 8.0F, 4.0F, 8.0F, size, false);
//
//        core = new ModelPart(this);
//        core.setPos(-8.0F, 17.4F, 8.0F);
//        core.texOffs(50, 35).addBox(5.0F, -7.9F, -9.5F, 6.0F, 1.0F, 1.0F, size, false);
//        core.texOffs(52, 29).addBox(6.5F, -8.9F, -10.5F, 3.0F, 3.0F, 3.0F, size, false);
//
//        leg_left = new ModelPart(this);
//        leg_left.setPos(-8.0F, 18.4F, 8.0F);
//        leg_left.texOffs(49, 5).addBox(11.0F, -16.4F, -10.5F, 2.0F, 16.0F, 3.0F, size, false);
//        leg_left.texOffs(56, 37).addBox(11.0F, 1.6F, -13.0F, 2.0F, 4.0F, 2.0F, size, true);
//        leg_left.texOffs(56, 43).addBox(11.0F, 1.6F, -7.0F, 2.0F, 4.0F, 2.0F, size, false);
//        leg_left.texOffs(0, 42).addBox(11.0F, -0.4F, -13.0F, 2.0F, 2.0F, 8.0F, size, false);
//
//        leg_right = new ModelPart(this);
//        leg_right.setPos(-8.0F, 17.4F, 8.0F);
//        leg_right.texOffs(56, 37).addBox(3.0F, 2.6F, -13.0F, 2.0F, 4.0F, 2.0F, size, false);
//        leg_right.texOffs(0, 42).addBox(3.0F, 0.6F, -13.0F, 2.0F, 2.0F, 8.0F, size, false);
//        leg_right.texOffs(56, 43).addBox(3.0F, 2.6F, -7.0F, 2.0F, 4.0F, 2.0F, size, false);
//        leg_right.texOffs(54, 5).addBox(3.0F, -15.4F, -10.5F, 2.0F, 16.0F, 3.0F, size, false);
//
//        bone = new ModelPart(this);
//        bone.setPos(0.0F, 24.0F, 0.0F);
//        bone.texOffs(42, 0).addBox(-5.0F, -23.0F, -1.5F, 10.0F, 4.0F, 1.0F, size, false);
//    }
//
//    @Override
//    public void setupAnim(InfinityNukeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        //previously the render function, render code was moved to a method below
//    }
//
//    @Override
//    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        top.render(matrixStack, buffer, packedLight, packedOverlay);
//        bottom.render(matrixStack, buffer, packedLight, packedOverlay);
//        core.render(matrixStack, buffer, packedLight, packedOverlay);
//        leg_left.render(matrixStack, buffer, packedLight, packedOverlay);
//        leg_right.render(matrixStack, buffer, packedLight, packedOverlay);
//        bone.render(matrixStack, buffer, packedLight, packedOverlay);
//    }
//
//    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
//        modelRenderer.xRot = x;
//        modelRenderer.yRot = y;
//        modelRenderer.zRot = z;
//    }
}
