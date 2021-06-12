package com.buuz135.industrial.entity.client;


import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class InfinityLauncherProjectileModel extends EntityModel<InfinityLauncherProjectileEntity> {

    private final ModelRenderer bone;

    public InfinityLauncherProjectileModel() {
        textureWidth = 32;
        textureHeight = 32;

        bone = new ModelRenderer(this);
        bone.setRotationPoint(0.5F, 15.5F, 0.0F);
        bone.setTextureOffset(28, 3).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 9.0F, 1.0F, 0.0F, false);
        bone.setTextureOffset(0, 24).addBox(-2.5F, -4.5F, -2.5F, 5.0F, 3.0F, 5.0F, 0.0F, false);
        bone.setTextureOffset(24, 0).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(InfinityLauncherProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}