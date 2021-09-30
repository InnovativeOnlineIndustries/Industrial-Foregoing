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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public class InfinityTridentModel extends EntityModel<InfinityTridentEntity> {
	@Override
	public void setupAnim(InfinityTridentEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

	}

	@Override
	public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {

	}

//	private final ModelPart bone;
//
//	public InfinityTridentModel() {
//		texWidth = 64;
//		texHeight = 64;
//
//		bone = new ModelPart(this);
//		bone.setPos(0.0F, 24.0F, 0.0F);
//		bone.texOffs(28, 42).addBox(-9.0F, 8.0F, 7.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
//		bone.texOffs(20, 42).addBox(-9.0F, 0.0F, 7.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
//		bone.texOffs(0, 29).addBox(-10.0F, 14.0F, 6.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
//		bone.texOffs(16, 12).addBox(-10.0F, 6.0F, 6.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
//		bone.texOffs(12, 42).addBox(-9.0F, -14.0F, 7.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
//		bone.texOffs(8, 12).addBox(-4.5F, -28.0F, 7.0F, 2.0F, 15.0F, 2.0F, 0.0F, false);
//		bone.texOffs(16, 29).addBox(-9.0F, -32.0F, 7.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);
//		bone.texOffs(0, 42).addBox(-9.5F, -28.0F, 6.5F, 3.0F, 4.0F, 3.0F, 0.0F, false);
//		bone.texOffs(0, 12).addBox(-13.5F, -28.0F, 7.0F, 2.0F, 15.0F, 2.0F, 0.0F, false);
//		bone.texOffs(0, 0).addBox(-12.5F, -21.0F, 5.5F, 9.0F, 7.0F, 5.0F, 0.0F, false);
//		bone.texOffs(24, 29).addBox(-9.5F, -6.0F, 6.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
//		bone.texOffs(28, 0).addBox(-10.0F, 8.0F, 6.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
//	}
//
//	@Override
//	public void setupAnim(InfinityTridentEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
//		//previously the render function, render code was moved to a method below
//	}
//
//	@Override
//	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
//		bone.render(matrixStack, buffer, packedLight, packedOverlay);
//	}
//
//	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
//		modelRenderer.xRot = x;
//		modelRenderer.yRot = y;
//		modelRenderer.zRot = z;
//	}


}
