package com.buuz135.industrial.api.book.gui;

import com.buuz135.industrial.api.book.button.ItemStackButton;
import com.buuz135.industrial.api.book.button.TextureButton;
import com.buuz135.industrial.book.BookCategory;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GUIBookMain extends GUIBookBase {

    private HashMap<ItemStackButton, BookCategory> categoriesButtons = new LinkedHashMap<>();
    private GuiButton searchButton;

    public GUIBookMain() {
        super(null, null);
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = 0;
        int renderScale = (this.getGuiXSize() - 40) / 3;
        for (BookCategory category : BookCategory.values()) {
            ItemStackButton button = new ItemStackButton(-235 - i, 21 + this.getGuiLeft() + ((i % 3) * renderScale), 15 + this.getGuiTop() + ((i / 3) * (renderScale + 4)), renderScale, renderScale, category.getName(), category.getDisplay());
            this.buttonList.add(button);
            categoriesButtons.put(button, category);
            ++i;
        }
        searchButton = new TextureButton(-135, this.getGuiLeft() - 5, this.getGuiTop() + 2, 18, 10, BOOK_EXTRAS, 1, 27, "Search");
        this.buttonList.add(searchButton);
    }

    @Override
    public boolean hasPageLeft() {
        return false;
    }

    @Override
    public boolean hasPageRight() {
        return false;
    }

    @Override
    public boolean hasBackButton() {
        return false;
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }

    @Override
    public void drawScreenFront(int mouseX, int mouseY, float partialTicks) {
        super.drawScreenFront(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (categoriesButtons.containsKey(button)) {
            this.mc.displayGuiScreen(new GUIBookCantegoryEntries(this, this, categoriesButtons.get(button)));
        } else if (button == searchButton) {
            onBackButton();
        } else {
            super.actionPerformed(button);
        }

    }

    @Override
    public void onBackButton() {
        this.mc.displayGuiScreen(new GUIBookBase(this, this) {
            @Override
            public boolean hasSearchBar() {
                return true;
            }

            @Override
            public boolean hasPageRight() {
                return true;
            }

            @Override
            public void onRightButton() {
                onBackButton();
            }
        });
    }


}
