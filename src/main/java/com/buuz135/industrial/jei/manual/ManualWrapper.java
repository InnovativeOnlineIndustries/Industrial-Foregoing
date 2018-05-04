package com.buuz135.industrial.jei.manual;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.button.TextureButton;
import com.buuz135.industrial.api.book.gui.GUIBookPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.GUIBookMain;
import lombok.Getter;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ManualWrapper implements IRecipeWrapper {

    private final TextureButton button;
    @Getter
    private final CategoryEntry entry;
    private PageText text;
    private int extraLines;

    @SideOnly(Side.CLIENT)
    public ManualWrapper(CategoryEntry entry) {
        this.button = new TextureButton(-135, 0, 105, 18, 18, GUIBookMain.BOOK_EXTRAS, 1, 38, "Open Manual Entry");
        this.entry = entry;
        this.extraLines = 0;
        int index = 0;
        for (IPage page : entry.getPages()) {
            if (page instanceof PageText) {
                this.text = (PageText) page;
                break;
            }
        }
        while (index != -1) {
            index = text.getText().indexOf("\n", index);
            if (index != -1) {
                extraLines++;
                index += "\n".length();
            }
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, entry.getDisplay());
        ingredients.setOutput(ItemStack.class, entry.getDisplay());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        button.drawButton(minecraft, mouseX, mouseY, 0);
        int y = 20;
        minecraft.fontRenderer.drawString(TextFormatting.DARK_AQUA + entry.getName(), 0, y, 0xFFFFFF);
        minecraft.fontRenderer.drawSplitString((TextFormatting.DARK_GRAY + text.getText().substring(0, Math.min(200 - extraLines * 10, text.getText().length())) + (text.getText().length() > 200 - extraLines * 10 ? "..." : "")).replaceAll(TextFormatting.GOLD.toString(), TextFormatting.DARK_AQUA.toString()), 0, y + minecraft.fontRenderer.FONT_HEIGHT + 4, 160, 0xFFFFFF);

    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (button.isMouseOver()) return Arrays.asList(button.displayString);
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if (button.mousePressed(minecraft, mouseX, mouseY)) {
            button.playPressSound(minecraft.getSoundHandler());
            minecraft.displayGuiScreen(new GUIBookPage(null, null, entry));
            return true;
        }
        return false;
    }
}
