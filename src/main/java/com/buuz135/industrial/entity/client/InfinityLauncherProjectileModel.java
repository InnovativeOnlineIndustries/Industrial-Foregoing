package com.buuz135.industrial.entity.client;


import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public class InfinityLauncherProjectileModel extends EntityModel<InfinityLauncherProjectileEntity> {

    private final ModelPart bone;

    public InfinityLauncherProjectileModel() {
        texWidth = 32;
        texHeight = 32;

        bone = new ModelPart(this);
        bone.setPos(0.5F, 15.5F, 0.0F);
        bone.texOffs(28, 3).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 9.0F, 1.0F, 0.0F, false);
        bone.texOffs(0, 24).addBox(-2.5F, -4.5F, -2.5F, 5.0F, 3.0F, 5.0F, 0.0F, false);
        bone.texOffs(24, 0).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(InfinityLauncherProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}