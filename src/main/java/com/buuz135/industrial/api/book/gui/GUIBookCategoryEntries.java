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
