package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class WorkingAreaTESR extends TileEntityRenderer<IndustrialAreaWorkingTile> {

    @Override
    public void render(IndustrialAreaWorkingTile tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
        if (tileEntityIn == null || !tileEntityIn.isShowingArea()) return;
        VoxelShape shape = tileEntityIn.getWorkingArea();

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
        GlStateManager.matrixMode(5889);
        GlStateManager.disableTexture();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0F, 1.0F, 0.999F);
        ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        BlockPos blockpos = tileEntityIn.getPos();
        Color color = new Color(Math.abs(blockpos.getX() % 255), Math.abs(blockpos.getY() % 255), Math.abs(blockpos.getZ() % 255));
        double d0 = info.getProjectedView().x;
        double d1 = info.getProjectedView().y;
        double d2 = info.getProjectedView().z;
        RenderHelper.disableStandardItemLighting();
        this.setLightmapDisabled(true);

        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        Minecraft.getInstance().worldRenderer.drawShape(shape, -d0, -d1, -d2, (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.5F);
        renderFaces(shape.getBoundingBox(), -d0, -d1, -d2, (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.3F);
        this.setLightmapDisabled(false);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean isGlobalRenderer(IndustrialAreaWorkingTile te) {
        return true;
    }

    private void renderFaces(AxisAlignedBB pos, double x, double y, double z, float red, float green, float blue, float alpha) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        double x1 = pos.minX + x;
        double x2 = pos.maxX + x;
        double y1 = pos.minY + y;
        double y2 = pos.maxY + y;
        double z1 = pos.minZ + z;
        double z2 = pos.maxZ + z;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();

        buffer.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();


        buffer.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();

        buffer.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();


        buffer.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();

        buffer.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();

        Tessellator.getInstance().draw();
    }
}
