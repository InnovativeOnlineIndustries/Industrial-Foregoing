package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.tile.world.LaserBaseTile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;


public class LaserBaseSpecialRender extends TileEntitySpecialRenderer<LaserBaseTile> {


//    private void drawLine(Tessellator tess, Vector2d v1, Vector2d v2, float partial) {
//
//        tess.getBuffer().pos(v1.getX() + 0.5, 0.5, v1.getY() + 0.5).endVertex();
//        tess.getBuffer().pos(v2.getX() + 0.5, 0.5, v2.getY() + 0.5).endVertex();
//
//        tess.getBuffer().pos(xb, ya, za).endVertex();
//        tess.getBuffer().pos(xb, ya, zb).endVertex();
//
//        tess.getBuffer().pos(xb, yb, za).endVertex();
//        tess.getBuffer().pos(xb, yb, zb).endVertex();
//
//
//    }
//
//            if (Block.getBlockFromItem(te.getCreatingStack().getItem()) == Blocks.AIR) return; DISCARDED CODE/ FUTURE USE
//        GlStateManager.pushMatrix();
//        GlStateManager.disableLighting();
//        GlStateManager.enableBlend();
//    Tessellator tess = Tessellator.getInstance();
//    BlockPos pos = te.getPos().add(0, 1, 0);
//    int lightBlock = te.getWorld().getLightFor(EnumSkyBlock.BLOCK, pos);
//    int lightSky = te.getWorld().getLightFor(EnumSkyBlock.SKY, pos);
//    //if (lightBlock != 0) lightSky =0;
//    //if (lightSky > lightBlock) lightBlock = 0;
//    VertexBuffer buffer = tess.getBuffer();
//        buffer.setTranslation(x - 0.001f, y + 1, z - 0.001f);
//    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//    //RenderHelper.enableStandardItemLighting();
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 16 * 10, 16 * 15);
//
//    Block block = Block.getBlockFromItem(te.getCreatingStack().getItem());
//    IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(block.getDefaultState());
//    TextureAtlasSprite flow = model.getQuads(block.getDefaultState(), EnumFacing.UP, 0).get(0).getSprite();
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//    double posY = te.getCurrentWork() / (double) te.getMaxWork();
//    double uvY = 16 * posY;
//    float zero = -0.002f;
//    float one = 1.002f;
//        buffer.pos(zero, posY, one).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(one, posY, one).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(one, posY, zero).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(0)).endVertex();
//        buffer.pos(zero, posY, zero).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(0)).endVertex();
//    flow = model.getQuads(block.getDefaultState(), EnumFacing.NORTH, 0).get(0).getSprite();
//    //SOUTH
//        buffer.pos(zero, posY, one).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16 - uvY)).endVertex();//C
//        buffer.pos(zero, 0, one).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16)).endVertex();//D
//        buffer.pos(one, 0, one).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16)).endVertex();//A
//        buffer.pos(one, posY, one).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16 - uvY)).endVertex();//B
//    //NORTH
//        buffer.pos(one, posY, zero).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16 - uvY)).endVertex();
//        buffer.pos(one, 0, zero).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(zero, 0, zero).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(zero, posY, zero).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16 - uvY)).endVertex();
//    //WEST
//        buffer.pos(zero, posY, zero).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16 - uvY)).endVertex();
//        buffer.pos(zero, 0, zero).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(zero, 0, one).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(zero, posY, one).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16 - uvY)).endVertex();
//    //EAST
//        buffer.pos(one, 0, zero).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16)).endVertex();
//        buffer.pos(one, posY, zero).tex(flow.getInterpolatedU(16), flow.getInterpolatedV(16 - uvY)).endVertex();
//        buffer.pos(one, posY, one).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16 - uvY)).endVertex();
//        buffer.pos(one, 0, one).tex(flow.getInterpolatedU(0), flow.getInterpolatedV(16)).endVertex();
//
//
//        tess.draw();
//        buffer.setTranslation(0, 0, 0);
//        GlStateManager.enableLighting();
//        GlStateManager.disableBlend();
//        GlStateManager.popMatrix();

}