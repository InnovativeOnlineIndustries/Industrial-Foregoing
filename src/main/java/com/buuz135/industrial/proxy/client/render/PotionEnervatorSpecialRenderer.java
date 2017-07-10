package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.tile.magic.PotionEnervatorTile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class PotionEnervatorSpecialRenderer extends TileEntitySpecialRenderer<PotionEnervatorTile> {

//    @Override
//    public void renderTileEntityAt(PotionEnervatorTile te, double x, double y, double z, float partialTicks, int destroyStage) {
//        if (!te.hasWorld()) return;
//        GlStateManager.pushMatrix();
//        GlStateManager.disableLighting();
//        GlStateManager.enableBlend();
//        Tessellator tess = Tessellator.getInstance();
//        BufferBuilder buffer = tess.getBuffer();
//        buffer.setTranslation(x, y, z);
//        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
//        TextureAtlasSprite flow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FluidRegistry.WATER.getStill().toString());
//        Color color = new Color(FluidRegistry.WATER.getColor());
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//        double posY = .1 + (.8 * ((float) (te.getFluidTank().getFluid() == null ? 0 : te.getFluidTank().getFluid().amount) / (float) te.getFluidTank().getCapacity()));
//        buffer.pos(4F / 16F, posY, 12F / 16F).tex(flow.getInterpolatedU(4), flow.getInterpolatedV(12)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
//        buffer.pos(12F / 16F, posY, 12F / 16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(12)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
//        buffer.pos(12F / 16F, posY, 4F / 16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(4)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
//        buffer.pos(4F / 16F, posY, 4F / 16F).tex(flow.getInterpolatedU(4), flow.getInterpolatedV(4)).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
//        tess.draw();
//        buffer.setTranslation(0, 0, 0);
//        GlStateManager.enableLighting();
//        GlStateManager.disableBlend();
//        GlStateManager.popMatrix();
//    }
}
