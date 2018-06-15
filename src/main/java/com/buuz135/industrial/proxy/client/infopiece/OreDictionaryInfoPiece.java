package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.misc.OreDictionaryConverterTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

public class OreDictionaryInfoPiece extends BasicRenderedGuiPiece {

    private OreDictionaryConverterTile tile;

    public OreDictionaryInfoPiece(OreDictionaryConverterTile tile, int left, int top) {
        super(left, top, 131, 26, ClientProxy.GUI, 1, 187);
        this.tile = tile;
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
        if (!tile.getFilter().getStackInSlot(0).isEmpty()) {
            FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
            GlStateManager.pushMatrix();
            renderer.drawString(TextFormatting.DARK_GRAY + "MOD: " + tile.getModid(), 48, 26, 0xFFFFFF);
            renderer.drawString(TextFormatting.DARK_GRAY + "ORE: " + tile.getOreDict(), 48, 26 + renderer.FONT_HEIGHT, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
    }
}
