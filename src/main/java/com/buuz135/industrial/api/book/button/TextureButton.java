/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
