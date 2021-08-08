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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class BlackHoleUnitTESR extends TileEntityRenderer<BHTile> {

    public static RenderType createRenderType(ResourceLocation texture) {
        RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false)).transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
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
        return RenderType.makeType("black_hole_label", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 32, false, true, state);
    }

    public BlackHoleUnitTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(BHTile tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!tile.getDisplayStack().isEmpty() && tile.shouldDisplay()){
            ItemStack stack = tile.getDisplayStack();
            matrixStack.push();
            Direction facing = tile.getFacingDirection();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-180));
            if (facing == Direction.NORTH) {
                //matrixStack.translate(0, 0, 1.016 / 16D);
                matrixStack.translate(-1, 0, 0);
            }
            if (facing == Direction.EAST) {
                matrixStack.translate(-1, 0, -1);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-90));
            }
            if (facing == Direction.SOUTH) {
                matrixStack.translate(0, 0,-1);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-180));
            }
            if (facing == Direction.WEST) {
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
            }
            matrixStack.translate(0.5, 0.6, 0);
            if (stack.getItem() instanceof BlockItem){
                matrixStack.scale(0.35f, 0.35f, 0.35f);
            } else {
                matrixStack.scale(0.4f, 0.4f, 0.4f);
            }
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE, 0xF000F0, combinedOverlayIn, matrixStack, bufferIn);
            matrixStack.pop();
            renderText(matrixStack, bufferIn, combinedOverlayIn, new StringTextComponent(TextFormatting.WHITE + tile.getFormatedDisplayAmount()), facing, 0.015f);
        }
    }
    /* Thanks Mekanism */
    private void renderText(MatrixStack matrix, IRenderTypeBuffer renderer, int overlayLight, ITextComponent text, Direction side, float maxScale) {
        matrix.push();
        matrix.translate(0, -0.3725, 0);
        switch (side) {
            case SOUTH:
                matrix.translate(0, 1, 0.0001);
                matrix.rotate(Vector3f.XP.rotationDegrees(90));
                break;
            case NORTH:
                matrix.translate(1, 1, 0.9999);
                matrix.rotate(Vector3f.YP.rotationDegrees(180));
                matrix.rotate(Vector3f.XP.rotationDegrees(90));
                break;
            case EAST:
                matrix.translate(0.0001, 1, 1);
                matrix.rotate(Vector3f.YP.rotationDegrees(90));
                matrix.rotate(Vector3f.XP.rotationDegrees(90));
                break;
            case WEST:
                matrix.translate(0.9999, 1, 0);
                matrix.rotate(Vector3f.YP.rotationDegrees(-90));
                matrix.rotate(Vector3f.XP.rotationDegrees(90));
                break;
        }

        float displayWidth = 1;
        float displayHeight = 1;
        matrix.translate(displayWidth / 2, 1, displayHeight / 2);
        matrix.rotate(Vector3f.XP.rotationDegrees(-90));

        FontRenderer font = renderDispatcher.getFontRenderer();

        int requiredWidth = Math.max(font.getStringPropertyWidth(text), 1);
        int requiredHeight = font.FONT_HEIGHT + 2;
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
        font.func_243247_a(text, offsetX - realWidth / 2, 3 + offsetY - realHeight / 2, overlayLight,
                false, matrix.getLast().getMatrix(), renderer, false, 0, 0xF000F0);
        matrix.pop();
    }
}
