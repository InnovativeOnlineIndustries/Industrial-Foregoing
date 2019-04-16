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
package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FluidConveyorTESR extends TileEntitySpecialRenderer<TileEntityConveyor> {
    @Override
    public void render(TileEntityConveyor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (te.getTank().getFluidAmount() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            EnumFacing facing = te.getFacing();
            if (facing == EnumFacing.NORTH) {
                GlStateManager.translate(1, 0, 1);
                GlStateManager.rotate(180, 0, 1, 0);
            }
            if (facing == EnumFacing.EAST) {
                GlStateManager.translate(0, 0, 1);
                GlStateManager.rotate(90, 0, 1, 0);
            }
            if (facing == EnumFacing.WEST) {
                GlStateManager.translate(1, 0, 0);
                GlStateManager.rotate(-90, 0, 1, 0);
            }
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            Fluid fluid = te.getTank().getFluid().getFluid();
            TextureAtlasSprite flow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFlowing().toString());
            TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
            double posY = 2 / 16f - 1 / 32f;
            double right = 1 / 16f;
            double left = 15 / 16f;
            BlockConveyor.EnumSides sides = BlockConveyor.EnumSides.NONE;
            if (te.getWorld().getBlockState(te.getPos()).getBlock() instanceof BlockConveyor)
                sides = te.getWorld().getBlockState(te.getPos()).getBlock().getActualState(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos()).getValue(BlockConveyor.SIDES);
            if (sides == BlockConveyor.EnumSides.BOTH || sides == BlockConveyor.EnumSides.RIGHT) right = 0;
            if (sides == BlockConveyor.EnumSides.BOTH || sides == BlockConveyor.EnumSides.LEFT) left = 1;
            Color color = new Color(fluid.getColor(te.getTank().getFluid()));

            buffer.pos(right, posY, 0).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(0)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(left, posY, 0).tex(flow.getInterpolatedU(8), flow.getInterpolatedV(0)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(left, posY, 1).tex(flow.getInterpolatedU(8), flow.getInterpolatedV(8)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(right, posY, 1).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(8)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

            boolean shouldRenderNext = !(te.getWorld().getTileEntity(te.getPos().offset(facing)) instanceof TileEntityConveyor) || ((TileEntityConveyor) te.getWorld().getTileEntity(te.getPos().offset(facing))).getTank().getFluidAmount() <= 0;
            if (shouldRenderNext) {
                buffer.pos(right, posY, 1).tex(still.getInterpolatedU(0), still.getInterpolatedV(0)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(left, posY, 1).tex(still.getInterpolatedU(8), still.getInterpolatedV(0)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(left, 1 / 16D, 1).tex(still.getInterpolatedU(8), still.getInterpolatedV(8)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(right, 1 / 16D, 1).tex(still.getInterpolatedU(0), still.getInterpolatedV(8)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            }
            boolean shouldRenderPrev = !(te.getWorld().getTileEntity(te.getPos().offset(facing.getOpposite())) instanceof TileEntityConveyor) || ((TileEntityConveyor) te.getWorld().getTileEntity(te.getPos().offset(facing.getOpposite()))).getTank().getFluidAmount() <= 0;
            if (shouldRenderPrev) {
                buffer.pos(right, posY, 0).tex(still.getInterpolatedU(0), still.getInterpolatedV(0)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(left, posY, 0).tex(still.getInterpolatedU(8), still.getInterpolatedV(0)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(left, 1 / 16D, 0).tex(still.getInterpolatedU(8), still.getInterpolatedV(8)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(right, 1 / 16D, 0).tex(still.getInterpolatedU(0), still.getInterpolatedV(8)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }
}
