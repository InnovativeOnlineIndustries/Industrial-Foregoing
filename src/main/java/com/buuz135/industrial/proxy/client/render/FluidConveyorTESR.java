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

import com.buuz135.industrial.block.transport.ConveyorBlock;
import com.buuz135.industrial.block.transport.tile.ConveyorTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FluidConveyorTESR extends TileEntityRenderer<ConveyorTile> {

    public FluidConveyorTESR() {
        super(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void func_225616_a_(ConveyorTile te, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        if (te.getTank().getFluidAmount() > 0) {
            int x = te.getPos().getX();
            int y = te.getPos().getY();
            int z = te.getPos().getZ();
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float) x, (float) y, (float) z);
            Direction facing = te.getFacing();
            if (facing == Direction.NORTH) {
                RenderSystem.translatef(1, 0, 1);
                RenderSystem.rotatef(180, 0, 1, 0);
            }
            if (facing == Direction.EAST) {
                RenderSystem.translatef(0, 0, 1);
                RenderSystem.rotatef(90, 0, 1, 0);
            }
            if (facing == Direction.WEST) {
                RenderSystem.translatef(1, 0, 0);
                RenderSystem.rotatef(-90, 0, 1, 0);
            }
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
            } else {
                RenderSystem.shadeModel(GL11.GL_FLAT);
            }
            Texture texture = Minecraft.getInstance().getTextureManager().func_229267_b_(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            if (texture instanceof AtlasTexture) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                FluidStack fluid = te.getTank().getFluid();
                TextureAtlasSprite flow = ((AtlasTexture) texture).getSprite(fluid.getFluid().getAttributes().getFlowingTexture(fluid));
                TextureAtlasSprite still = ((AtlasTexture) texture).getSprite(fluid.getFluid().getAttributes().getStillTexture(fluid));
                double posY = 2 / 16f - 1 / 32f;
                double right = 1 / 16f;
                double left = 15 / 16f;
                ConveyorBlock.EnumSides sides = te.getWorld().getBlockState(te.getPos()).getBlock().getExtendedState(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos()).get(ConveyorBlock.SIDES);
                if (sides == ConveyorBlock.EnumSides.BOTH || sides == ConveyorBlock.EnumSides.RIGHT) right = 0;
                if (sides == ConveyorBlock.EnumSides.BOTH || sides == ConveyorBlock.EnumSides.LEFT) left = 1;
                Color color = new Color(fluid.getFluid().getAttributes().getColor(te.getTank().getFluid()));

                buffer.func_225582_a_(right, posY, 0).func_225583_a_(flow.getInterpolatedU(0), flow.getInterpolatedV(0)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.func_225582_a_(left, posY, 0).func_225583_a_(flow.getInterpolatedU(8), flow.getInterpolatedV(0)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.func_225582_a_(left, posY, 1).func_225583_a_(flow.getInterpolatedU(8), flow.getInterpolatedV(8)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.func_225582_a_(right, posY, 1).func_225583_a_(flow.getInterpolatedU(0), flow.getInterpolatedV(8)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

                boolean shouldRenderNext = !(te.getWorld().getTileEntity(te.getPos().offset(facing)) instanceof ConveyorTile) || ((ConveyorTile) te.getWorld().getTileEntity(te.getPos().offset(facing))).getTank().getFluidAmount() <= 0;
                if (shouldRenderNext) {
                    buffer.func_225582_a_(right, posY, 1).func_225583_a_(still.getInterpolatedU(0), still.getInterpolatedV(0)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.func_225582_a_(left, posY, 1).func_225583_a_(still.getInterpolatedU(8), still.getInterpolatedV(0)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.func_225582_a_(left, 1 / 16D, 1).func_225583_a_(still.getInterpolatedU(8), still.getInterpolatedV(8)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.func_225582_a_(right, 1 / 16D, 1).func_225583_a_(still.getInterpolatedU(0), still.getInterpolatedV(8)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                }
                boolean shouldRenderPrev = !(te.getWorld().getTileEntity(te.getPos().offset(facing.getOpposite())) instanceof ConveyorTile) || ((ConveyorTile) te.getWorld().getTileEntity(te.getPos().offset(facing.getOpposite()))).getTank().getFluidAmount() <= 0;
                if (shouldRenderPrev) {
                    buffer.func_225582_a_(right, posY, 0).func_225583_a_(still.getInterpolatedU(0), still.getInterpolatedV(0)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.func_225582_a_(left, posY, 0).func_225583_a_(still.getInterpolatedU(8), still.getInterpolatedV(0)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.func_225582_a_(left, 1 / 16D, 0).func_225583_a_(still.getInterpolatedU(8), still.getInterpolatedV(8)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.func_225582_a_(right, 1 / 16D, 0).func_225583_a_(still.getInterpolatedU(0), still.getInterpolatedV(8)).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                }

                tessellator.draw();
            }
            RenderSystem.popMatrix();
        }
    }
}
