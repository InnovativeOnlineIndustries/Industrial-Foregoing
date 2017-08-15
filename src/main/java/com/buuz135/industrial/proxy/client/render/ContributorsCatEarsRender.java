package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.proxy.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EnumPlayerModelParts;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class ContributorsCatEarsRender implements LayerRenderer<AbstractClientPlayer> {

    public static Contributors contributors;

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!Arrays.asList(contributors.uuid).contains(entitylivingbaseIn.getUniqueID().toString())) return;
        if (!entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        RenderHelper.disableStandardItemLighting();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
        GlStateManager.translate(0, -0.015f, 0);
        if (!entitylivingbaseIn.inventory.armorInventory.get(3).isEmpty()) GlStateManager.translate(0, -0.02f, 0);
        if (entitylivingbaseIn.isSneaking()) GlStateManager.translate(0, 0.27, 0);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(netHeadYaw, 0, -1, 0);
        GlStateManager.rotate(headPitch, 0, 0, -1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(ClientProxy.ears_baked, 0.5f, 255, 255, 255);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public class Contributors {

        public String[] uuid;
    }
}
