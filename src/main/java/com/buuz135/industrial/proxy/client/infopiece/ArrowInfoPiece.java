package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import net.minecraft.util.text.TextComponentTranslation;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.ButtonPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.Collections;

public abstract class ArrowInfoPiece extends ButtonPiece {

    private int textureX;
    private int textureY;
    private String display;

    public ArrowInfoPiece(int left, int top, int textureX, int textureY, String display) {
        super(left, top, 15, 15);
        this.textureX = textureX;
        this.textureY = textureY;
        this.display = display;
    }

    @Override
    protected void renderState(BasicTeslaGuiContainer<?> basicTeslaGuiContainer, boolean b, BoundingRectangle boundingRectangle) {

    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
        container.drawTexturedRect(this.getLeft() - 1, this.getTop() - 1, textureX, textureY, 15, 15);
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
        if (isInside(container, mouseX, mouseY)) {
            container.drawTooltip(Collections.singletonList(new TextComponentTranslation(display).getFormattedText()), mouseX - guiX, mouseY - guiY);
        }
    }
}
