package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.awt.*;

public class MachineWorkAreaRender {

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        int radius = 16;
        BlockPos playerBlockPos = Minecraft.getMinecraft().player.getPosition();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos pos = new BlockPos(x + playerBlockPos.getX(), y + playerBlockPos.getY(), z + playerBlockPos.getZ());
                    if (Minecraft.getMinecraft().world.getTileEntity(pos) != null && Minecraft.getMinecraft().world.getTileEntity(pos) instanceof WorkingAreaElectricMachine) {
                        WorkingAreaElectricMachine areaElectricMachine = (WorkingAreaElectricMachine) Minecraft.getMinecraft().world.getTileEntity(pos);
                        AxisAlignedBB axis = areaElectricMachine.getWorkingArea();
                        if (areaElectricMachine.isShowArea() && axis != null) {
                            GlStateManager.disableDepth();
                            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                            GL11.glDepthMask(false);
                            GL11.glPushMatrix();

                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            EntityPlayerSP player = Minecraft.getMinecraft().player;
                            Vec3d playerPos = player.getPositionEyes(event.getPartialTicks());
                            GL11.glTranslated(pos.getX() - playerPos.xCoord, pos.getY() - playerPos.yCoord + player.getEyeHeight(), pos.getZ() - playerPos.zCoord);
                            axis = new AxisAlignedBB(axis.minX - pos.getX(), axis.minY - pos.getY(), axis.minZ - pos.getZ(), axis.maxX - pos.getX(), axis.maxY - pos.getY(), axis.maxZ - pos.getZ());

                            int color = areaElectricMachine.getColor();
                            Color c = new Color(Color.HSBtoRGB(color % 200 / 200F, 0.6F, 1F));
                            GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 64);
                            GL11.glLineWidth(2F);
                            renderAreaOutline(axis);
                            GL11.glPopMatrix();
                            GL11.glDepthMask(true);
                            GL11.glPopAttrib();
                            GlStateManager.enableDepth();
                        }
//                        if (Minecraft.getMinecraft().world.getTileEntity(pos) instanceof LaserBaseTile) {
//                            EntityPlayerSP player = Minecraft.getMinecraft().player;
//                            IBlockState actualState = Blocks.DIAMOND_ORE.getDefaultState();
//                            Block block = actualState.getBlock();
//                            double posY = 0.5;//TODO
//                            if (actualState.getRenderType() == EnumBlockRenderType.MODEL/* || actualState.getRenderType() == EnumBlockRenderType.LIQUID*/) {
//                                BlockRenderLayer originalLayer = MinecraftForgeClient.getRenderLayer();
//
//                                for (BlockRenderLayer layer : BlockRenderLayer.values()) {
//                                    if (block.canRenderInLayer(actualState, layer)) {
//                                        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//                                        ForgeHooksClient.setRenderLayer(layer);
//                                        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
//                                        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
//                                        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
//                                        float brightness = 0.83f;
//                                        //BlockPos pos = holder.pos;
//                                        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//                                        GL11.glDepthMask(false);
//                                        GL11.glPushMatrix();
//                                        GlStateManager.translate(pos.getX() - dx - 0.002f, 1 + pos.getY() - dy, pos.getZ() - dz - 0.002f);
//
//                                        GlStateManager.scale(1.005f, posY, 1.005f);
//
//
//                                        //RenderHelper.disableStandardItemLighting();
//
//                                        if (layer == BlockRenderLayer.CUTOUT) {
//                                            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
//                                        }
//
//                                        GlStateManager.color(1f, 1f, 1f, 1f);
//                                        GlStateManager.rotate(-90, 0, 1, 0);
//                                        //Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(actualState), actualState, brightness, true);
//
//
//                                        if (layer == BlockRenderLayer.CUTOUT) {
//                                            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//                                        }
//
//                                        GL11.glPopMatrix();
//                                        GL11.glDepthMask(true);
//                                        GL11.glPopAttrib();
//                                    }
//                                }
//
//                                ForgeHooksClient.setRenderLayer(originalLayer);
//                            }
//
//
//                            GL11.glPushMatrix();
//                            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//                            GL11.glDepthMask(false);
//                            GL11.glEnable(GL11.GL_BLEND);
//                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                            GL11.glDisable(GL11.GL_TEXTURE_2D);
//
//                            Vec3d playerPos = player.getPositionEyes(event.getPartialTicks());
//                            GL11.glTranslated(pos.getX() - playerPos.xCoord, 2 + pos.getY() - playerPos.yCoord + player.getEyeHeight(), pos.getZ() - playerPos.zCoord);
//
//                            Color c = new Color(Color.HSBtoRGB(52 / 360F, 0.6F, 1F));
//                            GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 64);
//                            GL11.glLineWidth(2F);
//                            Tessellator tess = Tessellator.getInstance();
//                            tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
////                            for (Vector2d v : ((LaserDrillTile) areaElectricMachine).getBlockPos()) {
////                                drawLine(tess, v, ((LaserDrillTile) areaElectricMachine).getPoint(), tile);
////                            }
//                            tess.draw();
//                            GlStateManager.translate(0, -1, 0);
//                            GlStateManager.enableDepth();
//                            GL11.glDepthMask(false);
////                            float maxY= tile.getWorkEnergyStored() / (float) tile.getWorkEnergyCapacity();
//                            //renderAreaOutline(new AxisAlignedBB(0,0,0,1,maxY,1));
////                            tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
////                            GL11.glLineWidth(1F);
////
////                                for (float tempX = 0;  tempX < 1; tempX += (1f/16f)){
////                                    drawStaticVerticalLine(tess,tempX+0.001f,-0.001f,tile);
////                                    drawStaticVerticalLine(tess,tempX+0.001f,1.001f,tile);
////                                    drawStaticVerticalLine(tess,-0.001f,tempX+0.00f,tile);
////                                    drawStaticVerticalLine(tess,1.001f,tempX+0.001f,tile);
////                                }
////                                for (float tempY = 0;  tempY < 1; tempY += (1f/16f)){
////                                    drawStaticHorizontal(tess,-0.001f,0,-0.001f,1,tempY*maxY);
////                                }
////
////                            tess.draw();
//                            GL11.glPopMatrix();
//                            GL11.glDepthMask(true);
//                            GL11.glPopAttrib();
//
//                        }


                    }
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void drawStaticVerticalLine(Tessellator tess, float x, float z, LaserBaseTile tile) {
        tess.getBuffer().pos(x, 0, z).endVertex();
        //tess.getBuffer().pos(x, tile.getWorkEnergyStored() / (double) tile.getWorkEnergyCapacity(),z).endVertex();
    }

    private void drawStaticHorizontal(Tessellator tess, float startX, float startZ, float endX, float endZ, float y) {
        tess.getBuffer().pos(startX, y, startZ).endVertex();
        tess.getBuffer().pos(endX, y, endZ).endVertex();
    }

    private void drawLine(Tessellator tess, Vector2d v1, Vector3d v2, LaserBaseTile tile) {

        tess.getBuffer().pos(v1.getX() + 0.5, 0.5, v1.getY() + 0.5).endVertex();
        double x = ((int) v1.getX()) == -1 ? 0 : ((int) v1.getX()) == 2 ? 1 : v2.x;
        double z = (((int) v1.getY()) == -1 ? 0 : ((int) v1.getY()) == 2 ? 1 : v2.z);
        double time = (tile.getWorld().getWorldTime() % 80);
        if (x == 0) z = time < 40 ? time / 40f : 2 - time / 40f;
        else if (x == 1) z = time > 40 ? -1 + time / 40f : 1 - time / 40f;
        else if (z == 0) x = time > 40 ? -1 + time / 40f : 1 - time / 40f;
        else if (z == 1) x = time < 40 ? time / 40f : 2 - time / 40f;


        //tess.getBuffer().pos(x, -1 + tile.getWorkEnergyStored() / (double) tile.getWorkEnergyCapacity(), z).endVertex();
//
//        tess.getBuffer().pos(xb, ya, za).endVertex();
//        tess.getBuffer().pos(xb, ya, zb).endVertex();
//
//        tess.getBuffer().pos(xb, yb, za).endVertex();
//        tess.getBuffer().pos(xb, yb, zb).endVertex();


    }


    private void renderAreaOutline(AxisAlignedBB aabb) {
        Tessellator tess = Tessellator.getInstance();
        double xa = aabb.minX;
        double xb = aabb.maxX;
        double ya = aabb.minY;
        double yb = aabb.maxY;
        double za = aabb.minZ;
        double zb = aabb.maxZ;

        tess.getBuffer().begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(xa, ya, za).endVertex();
        tess.getBuffer().pos(xa, yb, za).endVertex();
        tess.getBuffer().pos(xb, yb, za).endVertex();
        tess.getBuffer().pos(xb, ya, za).endVertex();
        tess.getBuffer().pos(xa, ya, za).endVertex();

        tess.getBuffer().pos(xa, ya, zb).endVertex();
        tess.getBuffer().pos(xa, yb, zb).endVertex();
        tess.getBuffer().pos(xb, yb, zb).endVertex();
        tess.getBuffer().pos(xb, ya, zb).endVertex();
        tess.getBuffer().pos(xa, ya, zb).endVertex();
        tess.draw();

        tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(xa, yb, za).endVertex();
        tess.getBuffer().pos(xa, yb, zb).endVertex();

        tess.getBuffer().pos(xb, ya, za).endVertex();
        tess.getBuffer().pos(xb, ya, zb).endVertex();

        tess.getBuffer().pos(xb, yb, za).endVertex();
        tess.getBuffer().pos(xb, yb, zb).endVertex();

        tess.draw();

    }
}
