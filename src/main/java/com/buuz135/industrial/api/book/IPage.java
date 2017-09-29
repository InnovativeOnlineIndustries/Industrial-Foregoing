package com.buuz135.industrial.api.book;

import com.buuz135.industrial.api.book.gui.GUIBookBase;
import net.minecraft.client.gui.FontRenderer;

public interface IPage {

    void drawScreenPre(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer);

    void drawScreen(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer);

    void drawScreenPost(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer);
}
