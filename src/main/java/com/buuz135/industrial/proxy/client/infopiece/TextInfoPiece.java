package com.buuz135.industrial.proxy.client.infopiece;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

public class TextInfoPiece extends BasicRenderedGuiPiece {

    private IHasDisplayString string;
    private int id;

    public TextInfoPiece(IHasDisplayString string, int id, int left, int top) {
        super(left, top, 0, 0, null, 0, 0);
        this.string = string;
        this.id = id;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        GlStateManager.pushMatrix();
        GlStateManager.scale(1, 1, 1);
        renderer.drawString(string.getString(id), this.getLeft() + guiX, this.getTop() + guiY, 0xFFFFFF);
        GlStateManager.popMatrix();
    }
}
