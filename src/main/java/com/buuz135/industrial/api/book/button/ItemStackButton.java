package com.buuz135.industrial.api.book.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemStackButton extends GuiButton {

    private ItemStack stack;

    public ItemStackButton(int buttonId, int x, int y, String buttonText, ItemStack stack) {
        super(buttonId, x, y, buttonText);
        this.stack = stack;
    }

    public ItemStackButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, ItemStack stack) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.stack = stack;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.scale(width / 16f, width / 16f, width / 16f);
            GlStateManager.color(1f, 1f, 1f);
            mc.getRenderItem().renderItemIntoGUI(stack, (int) (x / (width / 16f)), (int) (y / (width / 16f)));
            GlStateManager.scale(1 / (width / 16f), 1 / (width / 16f), 1 / (width / 16f));
        }
    }
}
