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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class InfinityTridentModel extends EntityModel<InfinityTridentEntity> {

	private final ModelRenderer bone;

	public InfinityTridentModel() {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone.setTextureOffset(28, 42).addBox(-9.0F, 8.0F, 7.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(20, 42).addBox(-9.0F, 0.0F, 7.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(0, 29).addBox(-10.0F, 14.0F, 6.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
		bone.setTextureOffset(16, 12).addBox(-10.0F, 6.0F, 6.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
		bone.setTextureOffset(12, 42).addBox(-9.0F, -14.0F, 7.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(8, 12).addBox(-4.5F, -28.0F, 7.0F, 2.0F, 15.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(16, 29).addBox(-9.0F, -32.0F, 7.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(0, 42).addBox(-9.5F, -28.0F, 6.5F, 3.0F, 4.0F, 3.0F, 0.0F, false);
		bone.setTextureOffset(0, 12).addBox(-13.5F, -28.0F, 7.0F, 2.0F, 15.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(0, 0).addBox(-12.5F, -21.0F, 5.5F, 9.0F, 7.0F, 5.0F, 0.0F, false);
		bone.setTextureOffset(24, 29).addBox(-9.5F, -6.0F, 6.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
		bone.setTextureOffset(28, 0).addBox(-10.0F, 8.0F, 6.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(InfinityTridentEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bone.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}


}