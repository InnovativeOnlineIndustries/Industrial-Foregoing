package com.buuz135.industrial.api.book.page;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.gui.GUIBookBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class PageItemList implements IPage {

    private final List<ItemStack> itemStacks;
    private final String display;

    public PageItemList(List<ItemStack> itemStacks, String display) {
        this.itemStacks = itemStacks;
        this.display = display;
    }

    public static List<PageItemList> generatePagesFromItemStacks(List<ItemStack> stacks, String display) {
        List<PageItemList> pages = new ArrayList<>();
        while (stacks.size() > 49) {
            pages.add(new PageItemList(stacks.subList(0, 49), display));
            stacks = stacks.subList(49, stacks.size());
        }
        pages.add(new PageItemList(stacks, display));
        return pages;
    }

    @Override
    public void drawScreenPre(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {
        renderer.drawString(TextFormatting.DARK_GRAY + display, base.getGuiLeft() + 15, base.getGuiTop() + 24, 0xFFFFFF);
        RenderHelper.enableGUIStandardItemLighting();
        int itemsRow = (base.getGuiXSize() - 40) / 18;
        for (int pos = 0; pos < itemStacks.size(); ++pos) {
            base.mc.getRenderItem().renderItemIntoGUI(itemStacks.get(pos), base.getGuiLeft() + 15 + (pos % itemsRow) * 20, base.getGuiTop() + 35 + (pos / itemsRow) * 20);
        }
    }

    @Override
    public void drawScreen(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {

    }

    @Override
    public void drawScreenPost(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {
        for (int pos = 0; pos < itemStacks.size(); ++pos) {
            int itemsRow = (base.getGuiXSize() - 40) / 18;
            if (mouseX >= base.getGuiLeft() + 15 + (pos % itemsRow) * 20 && mouseX <= base.getGuiLeft() + 15 + (pos % itemsRow) * 20 + 16 && mouseY >= base.getGuiTop() + 35 + (pos / itemsRow) * 20 && mouseY <= base.getGuiTop() + 35 + (pos / itemsRow) * 20 + 16) {
                base.drawHoveringText(itemStacks.get(pos).getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL), mouseX, mouseY);
                break;
            }
        }
    }
}
