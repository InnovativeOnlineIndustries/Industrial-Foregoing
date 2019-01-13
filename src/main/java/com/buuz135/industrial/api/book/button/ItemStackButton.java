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
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemStackButton extends GuiButton {

    private ItemStack stack;

    public ItemStackButton(int buttonId, int x, int y, String buttonText, ItemStack stack) {
        super(buttonId, x, y, buttonText);
        this.stack = stack;
    }

    public ItemStackButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, ItemStack stack) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.stack = stack;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.scale(width / 16f, width / 16f, width / 16f);
            GlStateManager.color(1f, 1f, 1f);
            mc.getRenderItem().renderItemIntoGUI(stack, (int) (x / (width / 16f)), (int) (y / (width / 16f)));
            GlStateManager.scale(1 / (width / 16f), 1 / (width / 16f), 1 / (width / 16f));
        }
    }
}
