/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.proxy.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleVex extends Particle {

    private final Entity entity;
    private List<EnumFacing> directions;
    private List<Vec3d> lines;
    private boolean isDying = false;

    public ParticleVex(Entity entity) {
        super(entity.world, entity.posX + entity.world.rand.nextDouble() - 0.5, entity.posY + 1 + entity.world.rand.nextDouble() - 0.5, entity.posZ + entity.world.rand.nextDouble() - 0.5);
        this.entity = entity;
        directions = new ArrayList<>();
        EnumFacing prev = EnumFacing.NORTH;
        directions.add(0, prev);
        for (int i = 1; i < 50; i++) {
            prev = directions.get(i - 1);
            directions.add(i, world.rand.nextDouble() < 0.05 ? getRandomFacing(world.rand, prev) : prev);
        }
        calculateLines();
        this.particleMaxAge = 500;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (entityIn instanceof EntityPlayer && Minecraft.getMinecraft().player.getUniqueID().equals(entity.getUniqueID()) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && this.entity.getPosition().add(0, 1, 0).distanceSq(posX, posY, posZ) < 3)
            return;
        Tessellator.getInstance().draw();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        double x = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        double y = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        double z = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        buffer.setTranslation(-x, -y, -z);
        for (Vec3d line : lines) {
            buffer.pos(line.x, line.y, line.z).color(1f, 1f, 1f, 1f).endVertex();
        }
        Tessellator.getInstance().draw();
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.entity.getPosition().distanceSq(posX, posY, posZ) > 2) {
            isDying = true;
        }
        if (!isDying) {
            directions.add(0, world.rand.nextDouble() < 0.05 ? getRandomFacing(world.rand, directions.get(0)) : directions.get(0));
            directions.remove(50);
            Vec3d directionVector = new Vec3d(directions.get(0).getDirectionVec().getX(), directions.get(0).getDirectionVec().getY(), directions.get(0).getDirectionVec().getZ()).scale(0.01);
            this.setPosition(posX - directionVector.x, posY - directionVector.y, posZ - directionVector.z);
            calculateLines();
        } else {
            directions.remove(directions.size() - 1);
            calculateLines();
            if (directions.isEmpty()) this.setExpired();
        }
    }

    private EnumFacing getRandomFacing(Random random, EnumFacing opposite) {
        EnumFacing facing = EnumFacing.random(random);
        while (facing.getOpposite().equals(opposite)) facing = EnumFacing.random(random);
        return facing;
    }

    private void calculateLines() {
        lines = new ArrayList<>();
        if (directions.size() == 0) return;
        EnumFacing prev = directions.get(0);
        int currentPosition = 0;
        Vec3d prevBlockPos = new Vec3d(posX, posY, posZ);
        lines.add(prevBlockPos);
        for (int i = 1; i < directions.size(); i++) {
            if (!directions.get(i).equals(prev) || i == directions.size() - 1) {
                Vec3d directionVector = new Vec3d(prev.getDirectionVec().getX(), prev.getDirectionVec().getY(), prev.getDirectionVec().getZ()).scale(0.01);
                Vec3d endBlockPos = new Vec3d(prevBlockPos.x + directionVector.x * (i - currentPosition), prevBlockPos.y + directionVector.y * (i - currentPosition), prevBlockPos.z + directionVector.z * (i - currentPosition));
                lines.add(endBlockPos);
                prev = directions.get(i);
                currentPosition = i;
                prevBlockPos = endBlockPos;
            }
        }
    }
}
