package com.buuz135.industrial.api.book.page;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.gui.GUIBookBase;
import lombok.Getter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;

public class PageText implements IPage {

    public static TextFormatting COLOR = TextFormatting.DARK_GRAY;

    @Getter
    private String text;

    public PageText(String text) {
        this.text = text;
    }

    public static String bold(String s) {
        return TextFormatting.GOLD + s + TextFormatting.RESET + COLOR;
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
}
