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
package com.buuz135.industrial.api.book.page;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.gui.GUIBookBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PageText implements IPage {

    public static TextFormatting COLOR = TextFormatting.DARK_GRAY;
    public static TextFormatting HIGHLIGHT = TextFormatting.GOLD;

    private String text;

    public PageText(String text) {
        this.text = text;
    }

    public static String bold(String s) {
        return TextFormatting.GOLD + s + TextFormatting.RESET + COLOR;
    }

    public static List<IPage> createTranslatedPages(String string, String... params) {
        String translated = I18n.format(string, params).replaceAll("[{]", HIGHLIGHT.toString()).replaceAll("[}]", COLOR.toString()).replaceAll("@L@", "\n");
        return Arrays.stream(translated.split("(@PAGE@)")).map(PageText::new).collect(Collectors.toList());
    }

    @Override
    public void drawScreenPre(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {
        renderer.drawSplitString(COLOR + text, base.getGuiLeft() + 20, base.getGuiTop() + 25, base.getGuiXSize() - 35, 0xFFFFFF);
    }

    @Override
    public void drawScreen(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {

    }

    @Override
    public void drawScreenPost(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {

    }

    public String getText() {
        return text;
    }
}
