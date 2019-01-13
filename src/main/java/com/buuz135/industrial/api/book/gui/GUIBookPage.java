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
package com.buuz135.industrial.api.book.gui;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.button.TextureButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

public class GUIBookPage extends GUIBookBase {

    private CategoryEntry entry;
    private int page;

    public GUIBookPage(GuiScreen prevScreen, GUIBookBase prevBase, CategoryEntry entry) {
        super(prevScreen, prevBase);
        this.page = 0;
        this.entry = entry;
    }


    @Override
    public void drawScreenBack(int mouseX, int mouseY, float partialTicks) {
        super.drawScreenBack(mouseX, mouseY, partialTicks);
        drawCenteredString(this.fontRenderer, TextFormatting.GOLD + "" + entry.getName(), this.getGuiLeft() + this.getGuiXSize() / 2, this.getGuiTop() + 12, 0xFFFFFF);
        GlStateManager.color(1f, 1f, 1f);
        entry.getPages().get(page).drawScreenPre(entry, this, mouseX, mouseY, partialTicks, this.fontRenderer);
    }

    @Override
    public void drawScreenFront(int mouseX, int mouseY, float partialTicks) {
        super.drawScreenFront(mouseX, mouseY, partialTicks);
        entry.getPages().get(page).drawScreenPost(entry, this, mouseX, mouseY, partialTicks, this.fontRenderer);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        entry.getPages().get(page).drawScreen(entry, this, mouseX, mouseY, partialTicks, this.fontRenderer);
        recreateButtons();
    }

    @Override
    public boolean hasPageLeft() {
        return page > 0;
    }

    @Override
    public boolean hasPageRight() {
        return page < entry.getPages().size() - 1;
    }

    @Override
    public boolean hasBackButton() {
        return true;
    }

    @Override
    public void onRightButton() {
        ++page;
    }

    @Override
    public void onLeftButton() {
        --page;
    }

    public void recreateButtons() {
        this.buttonList.removeIf(guiButton -> guiButton instanceof TextureButton);
        initGui();
    }
}
