package com.buuz135.industrial.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemStackRenderUtils {

    @SideOnly(Side.CLIENT)
    public static void renderItemIntoGUI(ItemStack stack, int x, int y, int gl) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        IBakedModel bakedmodel = renderItem.getItemModelWithOverrides(stack, null, null);
        GlStateManager.translate((float) x, (float) y, 100.0F + renderItem.zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);
        if (bakedmodel.isGui3d()) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            if (bakedmodel.isBuiltInRenderer()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                TileEntityItemStackRenderer.instance.renderByItem(stack);
            } else {
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();
                vertexbuffer.begin(gl, DefaultVertexFormats.ITEM);
                for (EnumFacing enumfacing : EnumFacing.values()) {
                    renderQuads(vertexbuffer, bakedmodel.getQuads(null, enumfacing, 0L), -1, stack);
                }
                renderQuads(vertexbuffer, bakedmodel.getQuads(null, null, 0L), -1, stack);
                tessellator.draw();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }


    private static void renderQuads(VertexBuffer renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = (BakedQuad) quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex()) {
                k = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }
}
