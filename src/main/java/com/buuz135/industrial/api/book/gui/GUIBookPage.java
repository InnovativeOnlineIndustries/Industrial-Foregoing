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
