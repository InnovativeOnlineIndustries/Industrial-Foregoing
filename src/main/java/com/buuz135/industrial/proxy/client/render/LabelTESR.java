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

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.block.BlockLabel;
import com.buuz135.industrial.proxy.block.tile.TileEntityLabel;
import com.buuz135.industrial.proxy.client.infopiece.IHasDisplayStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.lwjgl.opengl.GL11;

/*
 * This code was not stolen from Refined Storage
 */
public class LabelTESR extends TileEntitySpecialRenderer<TileEntityLabel> {

    private static String getFormatedString(int amount, TileEntityLabel.FormatType type) {
        return type.getFormat().apply(amount);
    }

    @Override
    public void render(TileEntityLabel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (!te.getWorld().getBlockState(te.getPos()).getBlock().equals(BlockRegistry.blockLabel)) return;
        TileEntity tileEntity = te.getWorld().getTileEntity(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).getValue(BlockLabel.FACING)));
        if (tileEntity instanceof IHasDisplayStack) {
            ItemStack stack = ((IHasDisplayStack) tileEntity).getItemStack();
            if (!((IHasDisplayStack) tileEntity).getItemStack().isEmpty()) {
                GlStateManager.pushMatrix();
                if (Minecraft.isAmbientOcclusionEnabled()) {
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                } else {
                    GlStateManager.shadeModel(GL11.GL_FLAT);
                }
                GlStateManager.translate(x + 0.5, y + 0.45, z);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.depthMask(true);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1F, 1F, 1F, 1F);
                rotate(te.getWorld().getBlockState(te.getPos()).getValue(BlockLabel.FACING));
                if (!(stack.getItem() instanceof ItemBlock)) {
                    GlStateManager.translate(0, 0.1, 0);
                    GlStateManager.scale(0.70, 0.70, 0.70);
                }

                IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, Minecraft.getMinecraft().player);
                bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GROUND, false);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, bakedModel);

                GlStateManager.disableAlpha();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableLighting();
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
                GlStateManager.disableBlend();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.popMatrix();


                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5, y + 0.35, z);
                rotate(te.getWorld().getBlockState(te.getPos()).getValue(BlockLabel.FACING));
                GlStateManager.rotate(180F, 0, 1, 0);
                GlStateManager.rotate(180F, 0, 0, 1);

                float size = 0.00450F;
                float factor = 2.0f;
                GlStateManager.scale(size * factor, size * factor, size);
                String string = getFormatedString(((IHasDisplayStack) tileEntity).getDisplayAmount(), stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ? TileEntityLabel.FormatType.FLUID : te.getFormatType());
                Minecraft.getMinecraft().fontRenderer.drawString(string, -Minecraft.getMinecraft().fontRenderer.getStringWidth(string) / 2, 0, 0xFFFFFF);
                GlStateManager.popMatrix();
            }
        }
    }

    private void rotate(EnumFacing facing) {
        if (facing == EnumFacing.NORTH) {
            GlStateManager.translate(0, 0, 1.016 / 16D);
        }
        if (facing == EnumFacing.EAST) {
            GlStateManager.translate(6.99 / 16D, 0, 8 / 16D);
            GlStateManager.rotate(90, 0, -1, 0);
        }
        if (facing == EnumFacing.SOUTH) {
            GlStateManager.translate(0, 0, 15 / 16D);
            GlStateManager.rotate(180, 0, -1, 0);
        }
        if (facing == EnumFacing.WEST) {
            GlStateManager.translate(-6.99 / 16D, 0, 8 / 16D);
            GlStateManager.rotate(90, 0, 1, 0);
        }
    }
}
/*
 * Kappa
 */
