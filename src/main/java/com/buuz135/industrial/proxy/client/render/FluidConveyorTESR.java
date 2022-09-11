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

import com.buuz135.industrial.block.transportstorage.ConveyorBlock;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FluidConveyorTESR implements BlockEntityRenderer<ConveyorTile> {

    public static RenderType createRenderType(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    RenderSystem.disableCull();
                }, () -> {
                    RenderSystem.disableBlend();
                })).createCompositeState(true);
        return RenderType.create("conveyor_fluid", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 32, false, true, state);
    }

    public FluidConveyorTESR(BlockEntityRendererProvider.Context p_173540_) {
    }

    @Override
    public void render(ConveyorTile te, float p_225616_2_, PoseStack matrixStack, MultiBufferSource typeBuffer, int p_225616_5_, int p_225616_6_) {
        if (te.getTank().getFluidAmount() > 0) {
            int x = te.getBlockPos().getX();
            int y = te.getBlockPos().getY();
            int z = te.getBlockPos().getZ();
            matrixStack.pushPose();
            Direction facing = te.getFacing();
            if (facing == Direction.NORTH) {
                matrixStack.translate(1, 0, 1);
                //RenderSystem.rotatef(180, 0, 1, 0);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
            }
            if (facing == Direction.EAST) {
                matrixStack.translate(0, 0, 1);
                //RenderSystem.rotatef(90, 0, 1, 0);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
            }
            if (facing == Direction.WEST) {
                matrixStack.translate(1, 0, 0);
                //RenderSystem.rotatef(-90, 0, 1, 0);
                matrixStack.mulPose(Vector3f.YN.rotationDegrees(90));
            }
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas) {
                FluidStack fluid = te.getTank().getFluid();
                IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid.getFluid());
                TextureAtlasSprite flow = ((TextureAtlas) texture).getSprite(renderProperties.getFlowingTexture(fluid));
                TextureAtlasSprite still = ((TextureAtlas) texture).getSprite(renderProperties.getStillTexture(fluid));
                float posY = 2 / 16f - 1 / 32f;
                float right = 1 / 16f;
                float left = 15 / 16f;
                VertexConsumer buffer = typeBuffer.getBuffer(createRenderType(new ResourceLocation(flow.getName().getNamespace(), "textures/" + flow.getName().getPath() + ".png")));
                //ConveyorBlock.EnumSides sides = te.getWorld().getBlockState(te.getPos()).getBlock().getExtendedState(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos()).get(ConveyorBlock.SIDES);
                ConveyorBlock.EnumSides sides = ConveyorBlock.EnumSides.NONE;
                if (sides == ConveyorBlock.EnumSides.BOTH || sides == ConveyorBlock.EnumSides.RIGHT) right = 0;
                if (sides == ConveyorBlock.EnumSides.BOTH || sides == ConveyorBlock.EnumSides.LEFT) left = 1;
                Color color = new Color(renderProperties.getTintColor(te.getTank().getFluid()));
                matrixStack.pushPose();
                Matrix4f matrix = matrixStack.last().pose();
                float animation = 16 * flow.uvShrinkRatio() * (te.getLevel().getGameTime() % flow.getFrameCount());

                buffer.vertex(matrix, left, posY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 0 + animation).endVertex();
                buffer.vertex(matrix, right, posY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0.5f, 0 + animation).endVertex();
                buffer.vertex(matrix, right, posY, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0.5f, 16f / (flow.getHeight() * flow.getFrameCount()) + animation).endVertex();
                buffer.vertex(matrix, left, posY, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 16f / (flow.getHeight() * flow.getFrameCount()) + animation).endVertex();

                buffer = typeBuffer.getBuffer(createRenderType(new ResourceLocation(still.getName().getNamespace(), "textures/" + still.getName().getPath() + ".png")));
                animation = still.uvShrinkRatio() * (te.getLevel().getGameTime() % (still.getFrameCount() * 16));
                boolean shouldRenderPrev = !(te.getLevel().getBlockEntity(te.getBlockPos().relative(facing.getOpposite())) instanceof ConveyorTile) || ((ConveyorTile) te.getLevel().getBlockEntity(te.getBlockPos().relative(facing.getOpposite()))).getTank().getFluidAmount() <= 0;
                if (shouldRenderPrev) {
                    buffer.vertex(matrix, right, posY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).endVertex();
                    buffer.vertex(matrix, left, posY, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1f, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).endVertex();
                    buffer.vertex(matrix, left, 1 / 16f, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1f, 1 - animation).endVertex();
                    buffer.vertex(matrix, right, 1 / 16f, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1 - animation).endVertex();
                }
                boolean shouldRenderNext = !(te.getLevel().getBlockEntity(te.getBlockPos().relative(facing)) instanceof ConveyorTile) || ((ConveyorTile) te.getLevel().getBlockEntity(te.getBlockPos().relative(facing))).getTank().getFluidAmount() <= 0;
                if (shouldRenderNext) {

                    buffer.vertex(matrix, left, posY, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1f, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).endVertex();
                    buffer.vertex(matrix, right, posY, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1 - 1f / (still.getHeight() * still.getFrameCount()) - animation).endVertex();
                    buffer.vertex(matrix, right, 1 / 16f, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1 - animation).endVertex();
                    buffer.vertex(matrix, left, 1 / 16f, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1f, 1 - animation).endVertex();
                }
                matrixStack.popPose();
            }
            matrixStack.popPose();
        }
    }
}
