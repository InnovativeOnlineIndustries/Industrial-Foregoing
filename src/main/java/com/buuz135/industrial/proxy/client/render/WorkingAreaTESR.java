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

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;

import java.awt.*;

public class WorkingAreaTESR extends TileEntityRenderer<IndustrialAreaWorkingTile> {

    public static RenderType createRenderType() {
        RenderType.State state = RenderType.State.getBuilder().transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableAlphaTest();
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.disableAlphaTest();
        })).build(true);
        return RenderType.makeType("working_area_render", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, state);
    }

    public WorkingAreaTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(IndustrialAreaWorkingTile tileEntityIn, float p_225616_2_, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int p_225616_5_, int p_225616_6_) {
        if (tileEntityIn == null || !tileEntityIn.isShowingArea()) return;
        VoxelShape shape = tileEntityIn.getWorkingArea();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().getMainWindow().getFramebufferWidth() / 1920.0F * 2.5F));
        RenderSystem.disableTexture();
        RenderSystem.pushMatrix();
        BlockPos blockpos = tileEntityIn.getPos();
        Color color = new Color(Math.abs(blockpos.getX() % 255), Math.abs(blockpos.getY() % 255), Math.abs(blockpos.getZ() % 255));
        RenderHelper.disableStandardItemLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.getLines());
        WorldRenderer.drawBoundingBox(stack, builder, shape.getBoundingBox().offset((double) -blockpos.getX(), (double) -blockpos.getY(), (double) -blockpos.getZ()), (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.5F);
        renderFaces(stack, renderTypeBuffer, shape.getBoundingBox(), (double) -blockpos.getX(), (double) -blockpos.getY(), (double) -blockpos.getZ(), (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.3F);
        RenderSystem.popMatrix();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isGlobalRenderer(IndustrialAreaWorkingTile te) {
        return true;
    }

    private void renderFaces(MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, AxisAlignedBB pos, double x, double y, double z, float red, float green, float blue, float alpha) {

        float x1 = (float) (pos.minX + x);
        float x2 = (float) (pos.maxX + x);
        float y1 = (float) (pos.minY + y);
        float y2 = (float) (pos.maxY + y);
        float z1 = (float) (pos.minZ + z);
        float z2 = (float) (pos.maxZ + z);

        Matrix4f matrix = stack.getLast().getMatrix();
        IVertexBuilder buffer = renderTypeBuffer.getBuffer(createRenderType());

        buffer.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();

        buffer.pos(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();


        buffer.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();

        buffer.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();


        buffer.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();

        buffer.pos(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();

    }
}
