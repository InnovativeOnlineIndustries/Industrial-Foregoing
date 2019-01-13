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

import com.buuz135.industrial.api.book.IBookCategory;
import com.buuz135.industrial.api.book.button.CategoryEntryButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GUIBookCategoryEntries extends GUIBookBase {

    public static final int SPACE = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 8;

    private IBookCategory category;

    public GUIBookCategoryEntries(GuiScreen prevScreen, GUIBookBase prevBase, IBookCategory category) {
        super(prevScreen, prevBase);
        this.category = category;
    }

    @Override
    public void initGui() {
        super.initGui();
        int entryPos = 0;
        for (ResourceLocation location : category.getEntries().keySet()) {
            this.addButton(new CategoryEntryButton(-315 - entryPos, this.getGuiLeft() + 16, this.getGuiTop() + 16 + SPACE * entryPos, this.getGuiXSize() - 32, SPACE, category.getEntries().get(location).getName(), category.getEntries().get(location)));
            ++entryPos;
        }

    }

    @Override
    public boolean hasBackButton() {
        return true;
    }
}
