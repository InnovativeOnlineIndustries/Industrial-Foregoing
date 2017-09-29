package com.buuz135.industrial.api.book.gui;

import com.buuz135.industrial.api.book.button.CategoryEntryButton;
import com.buuz135.industrial.book.BookCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GUIBookCantegoryEntries extends GUIBookBase {

    public static final int SPACE = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 8;

    private BookCategory category;

    public GUIBookCantegoryEntries(GuiScreen prevScreen, GUIBookBase prevBase, BookCategory category) {
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
