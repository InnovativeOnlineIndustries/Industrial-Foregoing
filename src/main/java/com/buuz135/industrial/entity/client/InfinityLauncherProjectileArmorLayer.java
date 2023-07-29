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

import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class InfinityLauncherProjectileArmorLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {

    public static HashMap<String, Integer> PROJECTILE_AMOUNT = new HashMap<>();

    private final EntityRenderDispatcher dispatcher;
    private InfinityLauncherProjectileEntity projectile;

    public InfinityLauncherProjectileArmorLayer(LivingEntityRenderer<T, M> p_174466_) {
        super(p_174466_);
        this.dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    protected int numStuck(T entity) {
        return PROJECTILE_AMOUNT.getOrDefault(entity.getUUID().toString(), 0);
    }

    @Override
    protected void renderStuckItem(PoseStack p_225632_1_, MultiBufferSource p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_) {
        float f = Mth.sqrt(p_225632_5_ * p_225632_5_ + p_225632_7_ * p_225632_7_);
        this.projectile = new InfinityLauncherProjectileEntity(p_225632_4_.level(), p_225632_4_.getX(), p_225632_4_.getY(), p_225632_4_.getZ());
        this.projectile.yRot = (float) (Math.atan2((double) p_225632_5_, (double) p_225632_7_) * (double) (180F / (float) Math.PI));
        this.projectile.xRot = (float) (Math.atan2((double) p_225632_6_, (double) f) * (double) (180F / (float) Math.PI));
        this.projectile.yRotO = this.projectile.yRot;
        this.projectile.xRotO = this.projectile.xRot;
        this.dispatcher.render(this.projectile, 0.0D, 0.0D, 0.0D, 0.0F, p_225632_8_, p_225632_1_, p_225632_2_, p_225632_3_);
    }
}
