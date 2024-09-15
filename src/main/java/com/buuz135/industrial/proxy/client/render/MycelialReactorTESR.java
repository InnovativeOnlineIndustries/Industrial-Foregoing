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

package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import java.awt.*;

public class MycelialReactorTESR implements BlockEntityRenderer<MycelialReactorTile> {

    public static RenderType TYPE = createRenderType();

    public static RenderType createRenderType() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/block/mycelial.png"), false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.depthMask(false);
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.SRC_ALPHA);
                }, () -> {
                    RenderSystem.disableBlend();
                })).createCompositeState(true);
        return RenderType.create("mycelial_render", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 262144, false, true, state);
    }

    public MycelialReactorTESR(BlockEntityRendererProvider.Context p_173540_) {

    }


    @Override
    public void render(MycelialReactorTile te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        //if (te.getBar().getProgress() == 0) return;
        stack.pushPose();
        stack.scale(1.5F, 1.5F, 1.5F);
        stack.translate(0.30, 1.75, 0.3D);
        stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        stack.mulPose(Axis.ZP.rotationDegrees((Minecraft.getInstance().level.getGameTime() / 20F) % 360));
        stack.mulPose(Axis.XP.rotationDegrees(-90f));

        double speed = 20;
        //if (true) speed = 20;
        float sin = ((float) Math.sin(te.getLevel().getGameTime() / speed)) * 0.05f;
        VertexConsumer buffer1 = buffer.getBuffer(TYPE);
        Matrix4f matrix = stack.last().pose();
        float pX1 = 1;
        float u = 1;
        float pX2 = 0;
        float u2 = 0;
        int color = Color.CYAN.getRGB();
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        float xOffset = -0.75f;
        float yOffset = -0f;
        float zOffset = -0.75f;
        int alpha = 255;
        buffer1.addVertex(matrix, pX2 + xOffset + sin, yOffset, 0 + zOffset + sin).setUv(u2, 0).setColor(red, green, blue, alpha);
        buffer1.addVertex(matrix, pX1 + xOffset - sin + 0.5f, yOffset, 0 + zOffset + sin).setUv(u, 0).setColor(red, green, blue, alpha);
        buffer1.addVertex(matrix, pX1 + xOffset - sin + 0.5f, yOffset, 1.5f + zOffset - sin).setUv(u, 1).setColor(red, green, blue, alpha);
        buffer1.addVertex(matrix, pX2 + xOffset + sin, yOffset, 1.5f + zOffset - sin).setUv(u2, 1).setColor(red, green, blue, alpha);
        yOffset = 0.01f;
        sin = ((float) Math.cos(te.getLevel().getGameTime() / speed)) * 0.05f;
        color = new Color(0xB578FF).getRGB();
        red = FastColor.ARGB32.red(color);
        green = FastColor.ARGB32.green(color);
        blue = FastColor.ARGB32.blue(color);
        buffer1.addVertex(matrix, pX2 + xOffset + sin, yOffset, 0 + zOffset + sin).setUv(u2, 0).setColor(red, green, blue, alpha);
        buffer1.addVertex(matrix, pX1 + xOffset - sin + 0.5f, yOffset, 0 + zOffset - sin).setUv(u, 0).setColor(red, green, blue, alpha);
        buffer1.addVertex(matrix, pX1 + xOffset + sin + 0.5f, yOffset, 1.5f + zOffset + sin).setUv(u, 1).setColor(red, green, blue, alpha);
        buffer1.addVertex(matrix, pX2 + xOffset - sin, yOffset, 1.5f + zOffset + sin).setUv(u2, 1).setColor(red, green, blue, alpha);
        stack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(MycelialReactorTile blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(4);
    }
}
