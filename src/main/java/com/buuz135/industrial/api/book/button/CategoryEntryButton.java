package com.buuz135.industrial.api.book.button;

import com.buuz135.industrial.api.book.CategoryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;

public class CategoryEntryButton extends GuiButton {

    private CategoryEntry entry;
    private boolean textTooBig;

    public CategoryEntryButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, CategoryEntry entry) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.entry = entry;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            this.hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.color(1f, 1f, 1f);
            mc.getRenderItem().renderItemIntoGUI(entry.getDisplay(), x, y);
            String displayString = TextFormatting.DARK_BLUE + entry.getName();
            int sw = mc.fontRenderer.getStringWidth(displayString);
            textTooBig = sw > width - 20;
            mc.fontRenderer.drawString(textTooBig ? displayString.substring(0, (displayString.length() * (width - 25)) / sw) + "..." : displayString, x + 20, y + 4, 0xFFFFFF);

        }
    }

    public CategoryEntry getEntry() {
        return entry;
    }

    public boolean isTextTooBig() {
        return textTooBig;
    }
}
