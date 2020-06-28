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
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FluidConveyorTESR extends TileEntityRenderer<ConveyorTile> {

    public static RenderType createRenderType(ResourceLocation texture) {
        RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false)).transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableAlphaTest();
            RenderHelper.disableStandardItemLighting();
            if (Minecraft.isAmbientOcclusionEnabled()) {
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
            } else {
                RenderSystem.shadeModel(GL11.GL_FLAT);
            }
            RenderSystem.disableCull();
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.disableAlphaTest();
        })).build(true);
        return RenderType.makeType("conveyor_fluid", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 32, false, true, state);
    }

    public FluidConveyorTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ConveyorTile te, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int p_225616_5_, int p_225616_6_) {
        if (te.getTank().getFluidAmount() > 0) {
            int x = te.getPos().getX();
            int y = te.getPos().getY();
            int z = te.getPos().getZ();
            RenderSystem.pushMatrix();
            Direction facing = te.getFacing();
            if (facing == Direction.NORTH) {
                matrixStack.translate(1, 0, 1);
                //RenderSystem.rotatef(180, 0, 1, 0);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
            }
            if (facing == Direction.EAST) {
                matrixStack.translate(0, 0, 1);
                //RenderSystem.rotatef(90, 0, 1, 0);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
            }
            if (facing == Direction.WEST) {
                matrixStack.translate(1, 0, 0);
                //RenderSystem.rotatef(-90, 0, 1, 0);
                matrixStack.rotate(Vector3f.YN.rotationDegrees(90));
            }
            Texture texture = Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            if (texture instanceof AtlasTexture) {
                FluidStack fluid = te.getTank().getFluid();
                TextureAtlasSprite flow = ((AtlasTexture) texture).getSprite(fluid.getFluid().getAttributes().getFlowingTexture(fluid));
                TextureAtlasSprite still = ((AtlasTexture) texture).getSprite(fluid.getFluid().getAttributes().getStillTexture(fluid));
                float posY = 2 / 16f - 1 / 32f;
                float right = 1 / 16f;
                float left = 15 / 16f;
                IVertexBuilder buffer = typeBuffer.getBuffer(createRenderType(new ResourceLocation(flow.getName().getNamespace(), "textures/" + flow.getName().getPath() + ".png")));
                //ConveyorBlock.EnumSides sides = te.getWorld().getBlockState(te.getPos()).getBlock().getExtendedState(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos()).get(ConveyorBlock.SIDES);
                ConveyorBlock.EnumSides sides = ConveyorBlock.EnumSides.NONE;
                if (sides == ConveyorBlock.EnumSides.BOTH || sides == ConveyorBlock.EnumSides.RIGHT) right = 0;
                if (sides == ConveyorBlock.EnumSides.BOTH || sides == ConveyorBlock.EnumSides.LEFT) left = 1;
                Color color = new Color(fluid.getFluid().getAttributes().getColor(te.getTank().getFluid()));
                matrixStack.push();
                Matrix4f matrix = matrixStack.getLast().getMatrix();
                float animation = 16 * flow.getUvShrinkRatio() * (te.getWorld().getGameTime() % flow.getFrameCount());
                buffer.pos(matrix, left, posY, 0).tex(0, 0 + animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(matrix, right, posY, 0).tex(0.5f, 0 + animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(matrix, right, posY, 1).tex(0.5f, 16f / (flow.getHeight() * flow.getFrameCount()) + animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(matrix, left, posY, 1).tex(0, 16f / (flow.getHeight() * flow.getFrameCount()) + animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

                buffer = typeBuffer.getBuffer(createRenderType(new ResourceLocation(still.getName().getNamespace(), "textures/" + still.getName().getPath() + ".png")));
                animation = still.getUvShrinkRatio() * (te.getWorld().getGameTime() % (still.getFrameCount() * 16));
                boolean shouldRenderPrev = !(te.getWorld().getTileEntity(te.getPos().offset(facing.getOpposite())) instanceof ConveyorTile) || ((ConveyorTile) te.getWorld().getTileEntity(te.getPos().offset(facing.getOpposite()))).getTank().getFluidAmount() <= 0;
                if (shouldRenderPrev) {
                    buffer.pos(matrix, right, posY, 0).tex(0, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.pos(matrix, left, posY, 0).tex(1f, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.pos(matrix, left, 1 / 16f, 0).tex(1f, 1 - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.pos(matrix, right, 1 / 16f, 0).tex(0, 1 - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                }
                boolean shouldRenderNext = !(te.getWorld().getTileEntity(te.getPos().offset(facing)) instanceof ConveyorTile) || ((ConveyorTile) te.getWorld().getTileEntity(te.getPos().offset(facing))).getTank().getFluidAmount() <= 0;
                if (shouldRenderNext) {
                    buffer.pos(matrix, left, posY, 1).tex(1f, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.pos(matrix, right, posY, 1).tex(0, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.pos(matrix, right, 1 / 16f, 1).tex(0, 1 - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    buffer.pos(matrix, left, 1 / 16f, 1).tex(1f, 1 - animation).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                }
                matrixStack.pop();
            }
            RenderSystem.popMatrix();
        }
    }
}
