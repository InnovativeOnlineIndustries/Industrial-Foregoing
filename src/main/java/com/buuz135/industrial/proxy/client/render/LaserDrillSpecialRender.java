package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import com.buuz135.industrial.tile.world.LaserDrillTile;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import javax.vecmath.Vector3d;


public class LaserDrillSpecialRender extends TileEntitySpecialRenderer<LaserDrillTile> {


    @Override
    public void renderTileEntityAt(LaserDrillTile tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (tile.getLaserBasePos() == null) {
            drawNameplate(tile, "No Laser Base", x, y, z, 16);
            return;
        }
        bindTexture(ClientProxy.beacon);
        BlockUtils.renderLaserBeam(tile, x, y, z, tile.getFacing().getOpposite(), partialTicks, 1);
    }

    private void drawLine(VertexBuffer tess, Vector3d v1, Vector3d v2, LaserBaseTile tile) {
        tess.pos(v1.getX(), v1.getY(), v1.getZ()).endVertex();
        System.out.println(v2.toString());
        double x = ((int) v1.getX()) == -1 ? 0 : ((int) v1.getX()) == 2 ? 1 : v2.x;
        double z = (((int) v1.getZ()) == -1 ? 0 : ((int) v1.getZ()) == 2 ? 1 : v2.z);
        double time = (tile.getWorld().getWorldTime() % 80);


        if (x == 0) z = time < 40 ? time / 40f : 2 - time / 40f;
        else if (x == -1) x = time > 40 ? -2 + time / 40f : -time / 40f;
        else if (z == 0) x = time > 40 ? -1 + time / 40f : 1 - time / 40f;
        else if (z == 1) x = time < 40 ? time / 40f : 2 - time / 40f;
        System.out.println(x + ":" + z);
        tess.pos(x, -1 + tile.getCurrentWork() / (double) tile.getMaxWork(), z).endVertex();
    }

//    TileEntity temp = tile.getWorld().getTileEntity(tile.getLaserBasePos()); FUTURE CODE DISCARDED
//        if (temp == null || !(temp instanceof LaserBaseTile)) return;
//    LaserBaseTile te = (LaserBaseTile) temp;
//        if (Block.getBlockFromItem(te.getCreatingStack().getItem()) == Blocks.AIR) return;
//        GL11.glPushMatrix();
//        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//        GL11.glDepthMask(false);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glColorMask(true,true,true,true);
//    Color c = new Color(Color.HSBtoRGB(52 / 360F, 0.6F, 1F));
//        GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 64);
//        GL11.glLineWidth(2F);
//    Tessellator tess = Tessellator.getInstance();
//    VertexBuffer buffer = tess.getBuffer();
//        buffer.setTranslation(x, y, z);
//    //RenderHelper.enableStandardItemLighting();
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
//    Vector3d v2 = new Vector3d(te.getPos().getX()-tile.getPos().getX(),0,te.getPos().getZ()-tile.getPos().getZ());
//        for (Vector3d v : tile.getPositions()) {
//        drawLine(buffer, v, v2, te);
//    }
//        tess.draw();
//        buffer.setTranslation(0, 0, 0);
//        GL11.glPopMatrix();
//        GL11.glDepthMask(true);
//        GL11.glPopAttrib();
}
