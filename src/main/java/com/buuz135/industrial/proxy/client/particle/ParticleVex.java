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

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleVex extends Particle {

    private final Entity entity;
    private List<Direction> directions;
    private List<Vec3d> lines;
    private boolean isDying = false;

    public ParticleVex(Entity entity) {
        super(entity.world, entity.getPosition().getX() + entity.world.rand.nextDouble() - 0.5, entity.getPosition().getY() + 1 + entity.world.rand.nextDouble() - 0.5, entity.getPosition().getZ() + entity.world.rand.nextDouble() - 0.5);
        this.entity = entity;
        directions = new ArrayList<>();
        Direction prev = Direction.NORTH;
        directions.add(0, prev);
        for (int i = 1; i < 50; i++) {
            prev = directions.get(i - 1);
            directions.add(i, world.rand.nextDouble() < 0.05 ? getRandomFacing(world.rand, prev) : prev);
        }
        calculateLines();
        this.maxAge = 500;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entity.getPosition().distanceSq(posX, posY, posZ, true) > 2) {
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

    @Override
    public void buildGeometry(IVertexBuilder buffer, ActiveRenderInfo entityIn, float p_225606_3_) {
        if (entityIn.getRenderViewEntity() instanceof ClientPlayerEntity && Minecraft.getInstance().player.getUniqueID().equals(entity.getUniqueID()) && !entityIn.isThirdPerson() && this.entity.getPosition().add(0, 1, 0).distanceSq(posX, posY, posZ, false) < 3)
            return;
        //RenderSystem.disableAlphaTest(); TODO
        //RenderSystem.enableBlend();
        //RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
        //        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        //RenderSystem.lineWidth(2.0F);
        //RenderSystem.disableTexture();
        //RenderSystem.color4f(1, 1, 1, 1);
        ////OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F); TODO
        //buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        //Entity playerEntity = entityIn.getRenderViewEntity();
        //double x = playerEntity.lastTickPosX + (entityIn.getProjectedView().x - playerEntity.lastTickPosX);
        //double y = playerEntity.lastTickPosY + (entityIn.getProjectedView().y - playerEntity.lastTickPosY);
        //double z = playerEntity.lastTickPosZ + (entityIn.getProjectedView().z - playerEntity.lastTickPosZ);
        //buffer.setTranslation(-x, -y, -z);
        //for (Vec3d line : lines) {
        //    buffer.pos(line.x, line.y, line.z).color(1f, 1f, 1f, 1f).endVertex();
        //}
        //Tessellator.getInstance().draw();
        //buffer.setTranslation(0.0D, 0.0D, 0.0D);
        //RenderSystem.enableTexture();
        //RenderSystem.disableBlend();
        //RenderSystem.enableAlphaTest();
    }


    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    private Direction getRandomFacing(Random random, Direction opposite) {
        Direction facing = Direction.random(random);
        while (facing.getOpposite().equals(opposite)) facing = Direction.random(random);
        return facing;
    }

    private void calculateLines() {
        lines = new ArrayList<>();
        if (directions.size() == 0) return;
        Direction prev = directions.get(0);
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
