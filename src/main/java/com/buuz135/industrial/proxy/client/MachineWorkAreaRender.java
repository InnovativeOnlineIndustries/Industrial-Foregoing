package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
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
                    }
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
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
