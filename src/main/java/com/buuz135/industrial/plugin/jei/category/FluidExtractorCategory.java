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
package com.buuz135.industrial.plugin.jei.category;

import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluidExtractorCategory implements IRecipeCategory<FluidExtractorRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "fluid_extractor");

    private IGuiHelper guiHelper;
    private IDrawable tankOverlay;

    public FluidExtractorCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.tankOverlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends FluidExtractorRecipe> getRecipeClass() {
        return FluidExtractorRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Fluid Extractor";
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 27, 76, 50).addPadding(0, 0, 0, 74).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(FluidExtractorRecipe fluidExtractorRecipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, new ArrayList<>(fluidExtractorRecipe.input.getStacks()));
        ingredients.setOutput(VanillaTypes.FLUID, fluidExtractorRecipe.output);
        if (!new ItemStack(fluidExtractorRecipe.result).isEmpty())
            ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(fluidExtractorRecipe.result));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FluidExtractorRecipe fluidExtractorRecipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, true, 0, 16);
        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 0) {
            guiItemStackGroup.init(1, false, 27, 34);
            guiItemStackGroup.setBackground(1, guiHelper.getSlotDrawable());
            guiItemStackGroup.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }

        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
        guiFluidStackGroup.init(1, false, 57, 1, 12, 48, Math.max(50, ingredients.getOutputs(VanillaTypes.FLUID).get(0).get(0).getAmount()), false, tankOverlay);

        guiItemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        guiFluidStackGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }

    @Override
    public void draw(FluidExtractorRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "Production: ", 80, 6, 0xFFFFFF);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "" + recipe.output.getAmount() + "mb/12ticks", 80, 6 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 1, 0xFFFFFF);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "" + "Average: ", 80, 6 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 2, 0xFFFFFF);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "" + ((int) (8 / recipe.breakChance) * recipe.output.getAmount()) + "mb", 80, 6 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3, 0xFFFFFF);
    }

    @Override
    public List<ITextComponent> getTooltipStrings(FluidExtractorRecipe recipe, double mouseX, double mouseY) {
        if (mouseX >= 78 && mouseX <= 140 && mouseY >= 5 && mouseY <= 25)
            return Arrays.asList(new StringTextComponent("Production rate"));
        if (mouseX >= 78 && mouseX <= 120 && mouseY >= 25 && mouseY <= 45)
            return Arrays.asList(new StringTextComponent("Average numbers aren't real numbers"));
        return new ArrayList<>();
    }
}
