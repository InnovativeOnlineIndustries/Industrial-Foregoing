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

import com.buuz135.industrial.block.transportstorage.tile.BHTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BlackHoleUnitTESR implements BlockEntityRenderer<BHTile> {

    public BlackHoleUnitTESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BHTile tile, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!tile.getDisplayStack().isEmpty() && tile.shouldDisplay()) {
            ItemStack stack = tile.getDisplayStack();
            matrixStack.pushPose();
            Direction facing = tile.getFacingDirection();
            matrixStack.mulPose(Axis.YP.rotationDegrees(-180));
            if (facing == Direction.NORTH) {
                //matrixStack.translate(0, 0, 1.016 / 16D);
                matrixStack.translate(-1, 0, 0);
            }
            if (facing == Direction.EAST) {
                matrixStack.translate(-1, 0, -1);
                matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
            }
            if (facing == Direction.SOUTH) {
                matrixStack.translate(0, 0, -1);
                matrixStack.mulPose(Axis.YP.rotationDegrees(-180));
            }
            if (facing == Direction.WEST) {
                matrixStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            matrixStack.translate(0.5, 0.6, 0);
            if (stack.getItem() instanceof BlockItem) {
                matrixStack.scale(0.35f, 0.35f, 0.35f);
            } else {
                matrixStack.scale(0.4f, 0.4f, 0.4f);
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, 0xF000F0, combinedOverlayIn, matrixStack, bufferIn, tile.getLevel(), 0);
            matrixStack.popPose();
            renderText(matrixStack, bufferIn, combinedOverlayIn, Component.literal(ChatFormatting.WHITE + tile.getFormatedDisplayAmount()), facing, 0.015f);
        }
    }

    /* Thanks Mekanism */
    private void renderText(PoseStack matrix, MultiBufferSource renderer, int overlayLight, Component text, Direction side, float maxScale) {
        matrix.pushPose();
        matrix.translate(0, -0.3725, 0);
        switch (side) {
            case SOUTH:
                matrix.translate(0, 1, 0.0001);
                matrix.mulPose(Axis.XP.rotationDegrees(90));
                break;
            case NORTH:
                matrix.translate(1, 1, 0.9999);
                matrix.mulPose(Axis.YP.rotationDegrees(180));
                matrix.mulPose(Axis.XP.rotationDegrees(90));
                break;
            case EAST:
                matrix.translate(0.0001, 1, 1);
                matrix.mulPose(Axis.YP.rotationDegrees(90));
                matrix.mulPose(Axis.XP.rotationDegrees(90));
                break;
            case WEST:
                matrix.translate(0.9999, 1, 0);
                matrix.mulPose(Axis.YP.rotationDegrees(-90));
                matrix.mulPose(Axis.XP.rotationDegrees(90));
                break;
        }

        float displayWidth = 1;
        float displayHeight = 1;
        matrix.translate(displayWidth / 2, 1, displayHeight / 2);
        matrix.mulPose(Axis.XP.rotationDegrees(-90));

        Font font = Minecraft.getInstance().font;

        int requiredWidth = Math.max(font.width(text), 1);
        int requiredHeight = font.lineHeight + 2;
        float scaler = 0.4F;
        float scaleX = displayWidth / requiredWidth;
        float scale = scaleX * scaler;
        if (maxScale > 0) {
            scale = Math.min(scale, maxScale);
        }

        matrix.scale(scale, -scale, scale);
        int realHeight = (int) Math.floor(displayHeight / scale);
        int realWidth = (int) Math.floor(displayWidth / scale);
        int offsetX = (realWidth - requiredWidth) / 2;
        int offsetY = (realHeight - requiredHeight) / 2;
        font.drawInBatch(text, offsetX - realWidth / 2f, 3 + offsetY - realHeight / 2f, overlayLight,
                false, matrix.last().pose(), renderer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        matrix.popPose();
    }
}
