package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class WorkingAreaTESR extends TileEntityRenderer<IndustrialAreaWorkingTile> {

    public WorkingAreaTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(IndustrialAreaWorkingTile tileEntityIn, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        if (tileEntityIn == null || !tileEntityIn.isShowingArea()) return;
        VoxelShape shape = tileEntityIn.getWorkingArea();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
        RenderSystem.matrixMode(5889);
        RenderSystem.disableTexture();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(1.0F, 1.0F, 0.999F);
        ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        BlockPos blockpos = tileEntityIn.getPos();
        Color color = new Color(Math.abs(blockpos.getX() % 255), Math.abs(blockpos.getY() % 255), Math.abs(blockpos.getZ() % 255));
        double d0 = info.getProjectedView().x;
        double d1 = info.getProjectedView().y;
        double d2 = info.getProjectedView().z;
        RenderHelper.disableStandardItemLighting();
        //this.setLightmapDisabled(true);

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        //TODO Minecraft.getInstance().worldRenderer.drawShape(shape, -d0, -d1, -d2, (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.5F);
        renderFaces(shape.getBoundingBox(), -d0, -d1, -d2, (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.3F);
        //this.setLightmapDisabled(false);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
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

        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).endVertex();

        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).endVertex();


        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).endVertex();

        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).endVertex();


        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).endVertex();

        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).endVertex();

        Tessellator.getInstance().draw();
    }
}
