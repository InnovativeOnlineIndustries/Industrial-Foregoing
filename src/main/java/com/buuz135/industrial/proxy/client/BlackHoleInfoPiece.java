package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.tile.misc.BlackHoleUnitTile;
import com.buuz135.industrial.utils.ItemStackRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

public class BlackHoleInfoPiece extends BasicRenderedGuiPiece {

    private BlackHoleUnitTile tile;

    public BlackHoleInfoPiece(BlackHoleUnitTile tile, int left, int top) {
        super(left, top, 147, 55, ClientProxy.GUI, 110, 1);
        this.tile = tile;
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
        if (this.tile != null) {
            FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.getLeft() + 2, this.getTop() + 8, 0);
            GlStateManager.scale(1, 1, 1);
            if (!tile.getStack().isEmpty()) {
                ItemStackRenderUtils.renderItemIntoGUI(tile.getStack(), 1, 0, 7);
                String display = new TextComponentTranslation(tile.getStack().getUnlocalizedName() + ".name").getUnformattedText();
                renderer.drawString(TextFormatting.DARK_GRAY + display.substring(0, Math.min(display.length(), 21)) + (display.length() > 21 ? "." : ""), 20, 4, 0xFFFFFF);
                renderer.drawString(TextFormatting.DARK_GRAY + new TextComponentTranslation("text.display.amount").getUnformattedText() + " " + tile.getAmount(), 4, (renderer.FONT_HEIGHT) * 3, 0xFFFFFF);
            } else
                renderer.drawString(TextFormatting.DARK_GRAY + new TextComponentTranslation("text.display.empty").getFormattedText(), 4, 4, 0xFFFFFF);

            GlStateManager.popMatrix();
        }
    }

}
