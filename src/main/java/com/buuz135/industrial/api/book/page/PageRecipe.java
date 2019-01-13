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
package com.buuz135.industrial.api.book.page;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.gui.GUIBookBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageRecipe implements IPage {

    public ResourceLocation recipeLocation;
    private IRecipe recipe;

    public PageRecipe(ResourceLocation recipeLocation) {
        this.recipeLocation = recipeLocation;
        this.recipe = ForgeRegistries.RECIPES.getValue(recipeLocation);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawScreenPre(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {
        //base.drawCenteredString(renderer, TextFormatting.AQUA+"Recipe"+TextFormatting.RESET, base.getGuiLeft()+base.getGuiXSize()/2,base.getGuiTop()+30,0xFFFFFF);
        base.mc.getTextureManager().bindTexture(GUIBookBase.BOOK_EXTRAS);
        base.drawTexturedModalRect(base.getGuiLeft() + 20, base.getGuiTop() + 64, 45, 1, 124, 62);
        int pos = 0;
        RenderHelper.enableGUIStandardItemLighting();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient != null && ingredient.getMatchingStacks().length > 0)
                base.mc.getRenderItem().renderItemIntoGUI(ingredient.getMatchingStacks()[0], base.getGuiLeft() + 25 + (pos % 3) * 18, base.getGuiTop() + 69 + (pos / 3) * 18);
            ++pos;
        }
        base.mc.getRenderItem().renderItemIntoGUI(recipe.getRecipeOutput(), base.getGuiLeft() + 25 + 94, base.getGuiTop() + 69 + 18);
    }

    @Override
    public void drawScreen(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawScreenPost(CategoryEntry entry, GUIBookBase base, int mouseX, int mouseY, float partialTicks, FontRenderer renderer) {
        for (int pos = 0; pos < 9; ++pos) {
            if (recipe.getIngredients().get(pos).getMatchingStacks().length == 0) continue;
            if (mouseX >= base.getGuiLeft() + 25 + (pos % 3) * 18 && mouseX <= base.getGuiLeft() + 25 + (pos % 3) * 18 + 16 && mouseY >= base.getGuiTop() + 69 + (pos / 3) * 18 && mouseY <= base.getGuiTop() + 69 + (pos / 3) * 18 + 16) {
                ItemStack stack = recipe.getIngredients().get(pos).getMatchingStacks()[0];
                base.drawHoveringText(stack.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL), mouseX, mouseY);
            }
        }
        if (mouseX >= base.getGuiLeft() + 25 + 94 && mouseX <= base.getGuiLeft() + 25 + 94 + 18 && mouseY >= base.getGuiTop() + 69 + 18 && mouseY <= base.getGuiTop() + 69 + 18 + 18) {
            base.drawHoveringText(recipe.getRecipeOutput().getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL), mouseX, mouseY);
        }
    }
}
