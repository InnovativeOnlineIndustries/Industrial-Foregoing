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

import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

public class FluidSieveCategory implements IRecipeCategory<OreFluidEntrySieve> {


    private final IGuiHelper helper;
    private IDrawable tankOverlay;

    public FluidSieveCategory(IGuiHelper helper) {
        this.helper = helper;
        tankOverlay = helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public ResourceLocation getUid() {
        return IndustrialRecipeTypes.ORE_SIEVE.getUid();
    }

    @Override
    public Class<? extends OreFluidEntrySieve> getRecipeClass() {
        return IndustrialRecipeTypes.ORE_SIEVE.getRecipeClass();
    }

    @Override
    public RecipeType<OreFluidEntrySieve> getRecipeType() {
        return IndustrialRecipeTypes.ORE_SIEVE;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent(ModuleResourceProduction.FLUID_SIEVING_MACHINE.getLeft().get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return helper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 4, 78, 47, 50).addPadding(0,4,0,20).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OreFluidEntrySieve recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 36).addIngredients(Ingredient.of(recipe.getSieveItem()));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).setFluidRenderer(200, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(ForgeTypes.FLUID_STACK, recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 17).addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutput());
    }

    @Override
    public void draw(OreFluidEntrySieve recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        SlotsScreenAddon.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 24, 36, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.BLUE.getFireworkColor()), integer -> true);
        SlotsScreenAddon.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 50, 17, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true);

    }
}
