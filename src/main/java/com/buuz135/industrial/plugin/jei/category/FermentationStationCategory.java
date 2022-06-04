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

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class FermentationStationCategory implements IRecipeCategory<OreFluidEntryFermenter> {

    private final IGuiHelper helper;
    private IDrawable tankOverlay;

    public FermentationStationCategory(IGuiHelper helper) {
        this.helper = helper;
        tankOverlay = helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public ResourceLocation getUid() {
        return IndustrialRecipeTypes.FERMENTER.getUid();
    }

    @Override
    public Class<? extends OreFluidEntryFermenter> getRecipeClass() {
        return IndustrialRecipeTypes.FERMENTER.getRecipeClass();
    }

    @Override
    public RecipeType<OreFluidEntryFermenter> getRecipeType() {
        return IndustrialRecipeTypes.FERMENTER;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent(ModuleResourceProduction.FERMENTATION_STATION.getLeft().get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        int offset = 45;
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 142 + offset, 29, 112 - offset, 60);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }
/*
    @Override
    public void setIngredients(OreFluidEntryFermenter oreWasherWrapper, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.FLUID, oreWasherWrapper.getInput());
        iIngredients.setOutput(VanillaTypes.FLUID, oreWasherWrapper.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OreFluidEntryFermenter recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
        guiFluidStackGroup.init(1, true, 47 - 45, 1, 12, 48, 200, false, tankOverlay);
        guiFluidStackGroup.init(2, false, 99- 45, 1, 12, 48, 200, false, tankOverlay);

        guiFluidStackGroup.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        guiFluidStackGroup.set(2, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }*/

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OreFluidEntryFermenter recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 47 - 45, 1).setFluidRenderer(200, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(VanillaTypes.FLUID, recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99- 45, 1).setFluidRenderer(200, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(VanillaTypes.FLUID, recipe.getOutput());
    }

    @Override
    public void draw(OreFluidEntryFermenter recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Minecraft.getInstance().font.draw(stack, ChatFormatting.DARK_AQUA + "Up to 500mb", 3, 52, 0xFFFFFF);
    }
}
