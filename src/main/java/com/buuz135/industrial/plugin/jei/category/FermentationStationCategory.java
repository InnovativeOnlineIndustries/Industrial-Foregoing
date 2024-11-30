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
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FermentationStationCategory implements IRecipeCategory<OreFluidEntryFermenter> {

    private final IGuiHelper helper;
    private IDrawable tankOverlay;

    public FermentationStationCategory(IGuiHelper helper) {
        this.helper = helper;
        tankOverlay = helper.createDrawable(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public RecipeType<OreFluidEntryFermenter> getRecipeType() {
        return IndustrialRecipeTypes.FERMENTER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModuleResourceProduction.FERMENTATION_STATION.getBlock().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        int offset = 45;
        return helper.createDrawable(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jei.png"), 142 + offset, 29, 112 - offset, 60);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OreFluidEntryFermenter recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 47 - 45, 1).setFluidRenderer(200, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99 - 45, 1).setFluidRenderer(200, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutput());
    }

    @Override
    public void draw(OreFluidEntryFermenter recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + Component.translatable("text.industrialforegoing.jei.recipe.up_to_500mb").getString(), 3, 52, 0xFFFFFF);
    }
}
