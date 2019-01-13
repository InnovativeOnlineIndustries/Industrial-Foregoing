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

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.button.CategoryEntryButton;
import com.buuz135.industrial.api.book.button.TextureButton;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class GUIBookBase extends GuiScreen {

    public static final ResourceLocation BOOK_EXTRAS = new ResourceLocation(IndustrialForegoingHelper.MOD_ID, "textures/gui/extras.png");
    public static final ResourceLocation BOOK_BACK = new ResourceLocation(IndustrialForegoingHelper.MOD_ID, "textures/gui/book_back.png");

    private GuiScreen prevScreen;
    private GUIBookBase prevBase;
    private int guiXSize;
    private int guiYSize;
    private int guiLeft;
    private int guiTop;

    private GuiButton back;
    private GuiButton left;
    private GuiButton right;
    private GuiTextField search;

    public GUIBookBase(GuiScreen prevScreen, GUIBookBase prevBase) {
        this.prevScreen = prevScreen;
        this.prevBase = prevBase;
        float scale = 0.65f;
        this.guiXSize = (int) (256 * scale);
        this.guiYSize = (int) (312 * scale);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.guiXSize) / 2;
        this.guiTop = (this.height - this.guiYSize) / 2;
        if (this.hasBackButton()) {
            back = new TextureButton(-135, this.guiLeft - 5, this.guiTop + 2, 18, 10, BOOK_EXTRAS, 1, 27, "Go back");
            this.buttonList.add(back);
        }
        if (this.hasPageLeft()) {
            left = new TextureButton(-136, this.guiLeft + 20, this.guiTop + guiYSize - 25, 18, 10, BOOK_EXTRAS, 1, 14, "Previous page");
            this.buttonList.add(left);
        }
        if (this.hasPageRight()) {
            right = new TextureButton(-137, this.guiLeft + guiXSize - 45, this.guiTop + guiYSize - 25, 18, 10, BOOK_EXTRAS, 1, 1, "Next page");
            this.buttonList.add(right);
        }
        if (hasSearchBar()) {
            search = new GuiTextField(-138, this.fontRenderer, this.guiLeft + 20, this.guiTop + 15, 128, 12);
            search.setMaxStringLength(50);
            search.setEnableBackgroundDrawing(true);
        }
    }

    public boolean hasPageLeft() {
        return false;
    }

    public boolean hasPageRight() {
        return false;
    }

    public boolean hasBackButton() {
        return false;
    }

    public boolean hasSearchBar() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawScreenBack(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawScreenFront(mouseX, mouseY, partialTicks);
    }

    public void drawScreenBack(int mouseX, int mouseY, float partialTicks) {
        this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, TextFormatting.DARK_AQUA + new TextComponentTranslation("text.industrialforegoing.book.title").getFormattedText(), this.getGuiLeft() + 85, this.getGuiTop() - 10, 0xFFFFFF);
        GlStateManager.color(1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(BOOK_BACK);
        drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0, 0, this.guiXSize, this.guiYSize, this.guiXSize, this.guiYSize);
        if (this.hasSearchBar()) {
            if (search.isFocused() && search.getText().isEmpty()) {

            }
            search.drawTextBox();
        }
    }

    public void drawScreenFront(int mouseX, int mouseY, float partialTicks) {
        this.buttonList.stream().filter(GuiButton::isMouseOver).forEach(guiButton -> {
            if (guiButton instanceof CategoryEntryButton) {
                //if (((CategoryEntryButton) guiButton).isTextTooBig())
                drawHoveringText(guiButton.displayString, guiButton.x + 8, guiButton.y + GUIBookCategoryEntries.SPACE - 1);
            } else {
                drawHoveringText(guiButton.displayString, mouseX, mouseY);
            }
        });
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (hasBackButton() && back == button) {
            onBackButton();
        }
        if (hasPageRight() && right == button) {
            onRightButton();
        }
        if (hasPageLeft() && left == button) {
            onLeftButton();
        }
        if (button instanceof CategoryEntryButton) {
            for (BookCategory category : BookCategory.values()) {
                for (ResourceLocation location : category.getEntries().keySet()) {
                    if (((CategoryEntryButton) button).getEntry().equals(category.getEntries().get(location))) {
                        this.mc.displayGuiScreen(new GUIBookPage(this, this, category.getEntries().get(location)));
                    }
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hasSearchBar()) {
            search.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (hasSearchBar()) search.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (hasSearchBar() && search.isFocused()) {
            search.textboxKeyTyped(typedChar, keyCode);
            updateGUIwithBar(search.getText());
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void updateGUIwithBar(String text) {
        if (!text.isEmpty()) {
            this.buttonList.removeIf(guiButton -> guiButton instanceof CategoryEntryButton);
            int buttonsAdded = 0;
            for (BookCategory category : BookCategory.values()) {
                for (ResourceLocation location : category.getEntries().keySet()) {
                    if (category.getEntries().get(location).getName().toLowerCase().contains(text.toLowerCase())) {
                        this.addButton(new CategoryEntryButton(-315 - buttonsAdded, this.getGuiLeft() + 16, this.getGuiTop() + 32 + GUIBookCategoryEntries.SPACE * buttonsAdded, this.getGuiXSize() - 32, GUIBookCategoryEntries.SPACE, category.getEntries().get(location).getName(), category.getEntries().get(location)));
                        ++buttonsAdded;
                        if (buttonsAdded >= 9) {
                            return;
                        }
                    }
                }
            }
            for (BookCategory category : BookCategory.values()) {
                for (ResourceLocation location : category.getEntries().keySet()) {
                    CategoryEntry entry = category.getEntries().get(location);
                    for (IPage page : entry.getPages()) {
                        if (page instanceof PageText) {
                            if (((PageText) page).getText().toLowerCase().contains(text.toLowerCase()) && buttonList.stream().filter(guiButton -> guiButton instanceof CategoryEntryButton).noneMatch(guiButton -> ((CategoryEntryButton) guiButton).getEntry().equals(entry))) {
                                this.addButton(new CategoryEntryButton(-315 - buttonsAdded, this.getGuiLeft() + 16, this.getGuiTop() + 32 + GUIBookCategoryEntries.SPACE * buttonsAdded, this.getGuiXSize() - 32, GUIBookCategoryEntries.SPACE, entry.getName(), entry));
                                ++buttonsAdded;
                                if (buttonsAdded >= 9) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onRightButton() {

    }

    public void onBackButton() {
        this.mc.displayGuiScreen(prevBase);
    }

    public void onLeftButton() {

    }

    public int getGuiXSize() {
        return guiXSize;
    }

    public int getGuiYSize() {
        return guiYSize;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }
}
