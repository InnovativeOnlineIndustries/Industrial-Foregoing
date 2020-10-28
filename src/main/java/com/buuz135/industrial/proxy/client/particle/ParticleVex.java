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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleVex extends Particle {

    private final Entity entity;
    private List<Direction> directions;
    private List<Vector3d> lines;
    private boolean isDying = false;

    public ParticleVex(Entity entity) { //getPosition
        super((ClientWorld) entity.world, entity.getPosX() + entity.world.rand.nextDouble() - 0.5, entity.getPosY() + 1 + entity.world.rand.nextDouble() - 0.5, entity.getPosZ() + entity.world.rand.nextDouble() - 0.5);
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
        if (!isDying && !this.isExpired) {
            directions.add(0, world.rand.nextDouble() < 0.05 ? getRandomFacing(world.rand, directions.get(0)) : directions.get(0));
            directions.remove(50);
            Vector3d directionVector = new Vector3d(directions.get(0).getDirectionVec().getX(), directions.get(0).getDirectionVec().getY(), directions.get(0).getDirectionVec().getZ()).scale(0.01);
            this.setPosition(posX - directionVector.x, posY - directionVector.y, posZ - directionVector.z);
            calculateLines();
        } else {
            directions.remove(directions.size() - 1);
            calculateLines();
            if (directions.isEmpty()) this.setExpired();
        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo activeRenderInfo, float v) {
        if (entity instanceof ClientPlayerEntity && Minecraft.getInstance().player.getUniqueID().equals(entity.getUniqueID()) && Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON && this.entity.getPosition().add(0, 1, 0).distanceSq(posX, posY, posZ, false) < 3)
            return;
        Vector3d vector3d = activeRenderInfo.getProjectedView();
        double x = entity.lastTickPosX + (vector3d.x - entity.lastTickPosX);
        double y = entity.lastTickPosY + (vector3d.y - entity.lastTickPosY);
        double z = entity.lastTickPosZ + (vector3d.z - entity.lastTickPosZ);
        for (Vector3d line : lines) {
            buffer.pos(line.x - x, line.y - y, line.z - z).color(1f, 1f, 1f, 1f).lightmap(240, 240).endVertex();
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return new IParticleRenderType() {
            @Override
            public void beginRender(BufferBuilder builder, TextureManager manager) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                RenderSystem.lineWidth(2.0F);
                RenderSystem.disableTexture();
                builder.begin(3, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
            }

            @Override
            public void finishRender(Tessellator tessellator) {
                tessellator.draw();
                RenderSystem.disableBlend();
                RenderSystem.enableTexture();
            }
        };
    }

    private Direction getRandomFacing(Random random, Direction opposite) {
        Direction facing = Direction.getRandomDirection(random); //random
        while (facing.getOpposite().equals(opposite)) facing = Direction.getRandomDirection(random);
        return facing;
    }

    private void calculateLines() {
        lines = new ArrayList<>();
        if (directions.size() == 0) return;
        Direction prev = directions.get(0);
        int currentPosition = 0;
        Vector3d prevBlockPos = new Vector3d(posX, posY, posZ);
        lines.add(prevBlockPos);
        for (int i = 1; i < directions.size(); i++) {
            if (!directions.get(i).equals(prev) || i == directions.size() - 1) {
                Vector3d directionVector = new Vector3d(prev.getDirectionVec().getX(), prev.getDirectionVec().getY(), prev.getDirectionVec().getZ()).scale(0.01);
                Vector3d endBlockPos = new Vector3d(prevBlockPos.x + directionVector.x * (i - currentPosition), prevBlockPos.y + directionVector.y * (i - currentPosition), prevBlockPos.z + directionVector.z * (i - currentPosition));
                lines.add(endBlockPos);
                prev = directions.get(i);
                currentPosition = i;
                prevBlockPos = endBlockPos;
            }
        }
    }
}
