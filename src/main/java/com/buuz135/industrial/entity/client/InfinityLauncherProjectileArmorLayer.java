package com.buuz135.industrial.entity.client;

import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

import java.util.HashMap;

public class InfinityLauncherProjectileArmorLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {

    public static HashMap<String, Integer> PROJECTILE_AMOUNT = new HashMap<>();

    private final EntityRenderDispatcher entityRendererManager;
    private InfinityLauncherProjectileEntity projectile;

    public InfinityLauncherProjectileArmorLayer(LivingEntityRenderer<T, M> livingRenderer) {
        super(livingRenderer);
        this.entityRendererManager = livingRenderer.getDispatcher();
    }

    @Override
    protected int numStuck(T entity) {
        return PROJECTILE_AMOUNT.getOrDefault(entity.getUUID().toString(), 0);
    }

    @Override
    protected void renderStuckItem(PoseStack p_225632_1_, MultiBufferSource p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_) {
        float f = Mth.sqrt(p_225632_5_ * p_225632_5_ + p_225632_7_ * p_225632_7_);
        this.projectile = new InfinityLauncherProjectileEntity(p_225632_4_.level, p_225632_4_.getX(), p_225632_4_.getY(), p_225632_4_.getZ());
        this.projectile.yRot = (float) (Math.atan2((double) p_225632_5_, (double) p_225632_7_) * (double) (180F / (float) Math.PI));
        this.projectile.xRot = (float) (Math.atan2((double) p_225632_6_, (double) f) * (double) (180F / (float) Math.PI));
        this.projectile.yRotO = this.projectile.yRot;
        this.projectile.xRotO = this.projectile.xRot;
        this.entityRendererManager.render(this.projectile, 0.0D, 0.0D, 0.0D, 0.0F, p_225632_8_, p_225632_1_, p_225632_2_, p_225632_3_);
    }
}
