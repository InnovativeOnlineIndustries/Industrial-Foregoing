package com.buuz135.industrial.api.book.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextureButton extends GuiButton {

    private ResourceLocation gui;
    private int textureX;
    private int textureY;

    public TextureButton(int buttonId, int x, int y, ResourceLocation resourceLocation, int textureX, int textureY, String buttonText) {
        super(buttonId, x, y, buttonText);
        this.gui = resourceLocation;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public TextureButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation resourceLocation, int textureX, int textureY, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.gui = resourceLocation;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(gui);
            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            this.drawTexturedModalRect(this.x, this.y, this.textureX + 23 * (hovered ? 1 : 0), this.textureY, this.width, this.height);
        }
    }


}
