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

package com.buuz135.industrial.proxy.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ParticleVex extends Particle {

    public static ParticleRenderType RENDER = new ParticleRenderType() {
        @Override
        public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.lineWidth(1.5F);
            return tesselator.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
        }
    };

    private final Entity entity;
    private List<Direction> directions;
    private List<Vector3f> lines;
    private boolean isDying = false;

    public ParticleVex(Entity entity) { //getPosition
        super((ClientLevel) entity.level(), entity.getX() + entity.level().random.nextDouble() - 0.5, entity.getY() + 1 + entity.level().random.nextDouble() - 0.5, entity.getZ() + entity.level().random.nextDouble() - 0.5);
        this.entity = entity;
        directions = new ArrayList<>();
        Direction prev = Direction.NORTH;
        directions.add(0, prev);
        for (int i = 1; i < 50; i++) {
            prev = directions.get(i - 1);
            directions.add(i, level.random.nextDouble() < 0.05 ? getRandomFacing(level.random, prev) : prev);
        }
        calculateLines();
        this.lifetime = 500;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entity.position().distanceToSqr(new Vec3(x, y, z)) > 10) {
            isDying = true;
        }
        if (!isDying && !this.removed) {
            directions.add(0, level.random.nextDouble() < 0.05 ? getRandomFacing(level.random, directions.get(0)) : directions.get(0));
            directions.remove(50);
            Vec3 directionVector = new Vec3(directions.get(0).getNormal().getX(), directions.get(0).getNormal().getY(), directions.get(0).getNormal().getZ()).scale(0.01);
            this.setPos(x - directionVector.x, y - directionVector.y, z - directionVector.z);
            calculateLines();
        } else {
            directions.remove(directions.size() - 1);
            calculateLines();
            if (directions.isEmpty()) this.remove();
        }
    }

    @Override
    public void render(VertexConsumer bufferBad, Camera activeRenderInfo, float v) {
        if (entity instanceof LocalPlayer && Minecraft.getInstance().player.getUUID().equals(entity.getUUID()) && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON && this.entity.position().add(0, 1, 0).distanceToSqr(new Vec3(x, y, z)) < 3)
            return;
        var bufferBuilder = RENDER.begin(Tesselator.getInstance(), Minecraft.getInstance().getTextureManager());

        Vec3 vector3d = activeRenderInfo.getPosition();
        float x = (float) (entity.xOld + (vector3d.x - entity.xOld));
        float y = (float) (entity.yOld + (vector3d.y - entity.yOld));
        float z = (float) (entity.zOld + (vector3d.z - entity.zOld));


        for (Vector3f line : lines) {
            bufferBuilder.addVertex(line.x - x, line.y - y, line.z - z).setColor(1f, 1f, 1f, 1f).setUv2(240, 240);
        }
        var mesh = bufferBuilder.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER;
    }

    private Direction getRandomFacing(RandomSource random, Direction opposite) {
        Direction facing = Direction.getRandom(random); //random
        while (facing.getOpposite().equals(opposite)) facing = Direction.getRandom(random);
        return facing;
    }

    private void calculateLines() {
        lines = new ArrayList<>();
        if (directions.size() == 0) return;
        Direction prev = directions.get(0);
        int currentPosition = 0;
        Vector3f prevBlockPos = new Vector3f((float) x, (float) y, (float) z);
        lines.add(prevBlockPos);
        for (int i = 1; i < directions.size(); i++) {
            if (!directions.get(i).equals(prev) || i == directions.size() - 1) {
                Vec3 directionVector = new Vec3(prev.getNormal().getX(), prev.getNormal().getY(), prev.getNormal().getZ()).scale(0.01);
                Vector3f endBlockPos = new Vector3f((float) (prevBlockPos.x + directionVector.x * (i - currentPosition)), (float) (prevBlockPos.y + directionVector.y * (i - currentPosition)), (float) (prevBlockPos.z + directionVector.z * (i - currentPosition)));
                lines.add(endBlockPos);
                prev = directions.get(i);
                currentPosition = i;
                prevBlockPos = endBlockPos;
            }
        }
    }
}
