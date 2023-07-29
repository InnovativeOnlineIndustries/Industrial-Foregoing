/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.plugin.jei.category;

import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.recipe.StoneWorkGenerateRecipe;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.LangUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StoneWorkGeneratorCategory implements IRecipeCategory<StoneWorkGenerateRecipe> {

    private final IGuiHelper helper;

    public StoneWorkGeneratorCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<StoneWorkGenerateRecipe> getRecipeType() {
        return IndustrialRecipeTypes.STONE_WORK_GENERATOR;
    }

    @Override
    public Component getTitle() {
        return Component.literal("StoneWork Generation");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(130, 9 * 6);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StoneWorkGenerateRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 9, 19).addIngredient(VanillaTypes.ITEM_STACK, recipe.output);
    }

    @Override
    public void draw(StoneWorkGenerateRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<Component> lines = new ArrayList<>();
        SlotsScreenAddon.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 8, 9 * 2, 0, 0, 1, integer -> Pair.of(1, 1), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true);
        lines.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.needs")));
        lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.waterNeed + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
        lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.lavaNeed + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
        lines.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.consumes")));
        lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.waterConsume + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
        lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.lavaConsume + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
        int y = 0;
        for (Component line : lines) {
            guiGraphics.drawString(Minecraft.getInstance().font, line.getString(), 36, y * Minecraft.getInstance().font.lineHeight, 0xFFFFFFFF);
            ++y;
        }
    }
}
