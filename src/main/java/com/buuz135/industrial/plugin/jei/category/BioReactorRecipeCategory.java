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

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class BioReactorRecipeCategory implements IRecipeCategory<BioReactorRecipeCategory.ReactorRecipeWrapper> {

    public static ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "bioreactor");

    private IGuiHelper guiHelper;
    private IDrawable tankOverlay;
    private String title;

    public BioReactorRecipeCategory(IGuiHelper guiHelper, String title) {
        this.guiHelper = guiHelper;
        tankOverlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
        this.title = title;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends ReactorRecipeWrapper> getRecipeClass() {
        return BioReactorRecipeCategory.ReactorRecipeWrapper.class;
    }

    @Override
    public String getTitle() {
        return title;
    }


    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 27, 82, 50);
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(ReactorRecipeWrapper reactorRecipeWrapper, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, reactorRecipeWrapper.getStack());
        iIngredients.setOutput(VanillaTypes.FLUID, reactorRecipeWrapper.getFluid());
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ReactorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, true, 0, 16);

        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
        guiFluidStackGroup.init(1, false, 57, 1, 12, 48, 200, false, tankOverlay);

        guiItemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        guiFluidStackGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }

    public static class ReactorRecipeWrapper {

        private ItemStack stack;
        private FluidStack fluid;

        public ReactorRecipeWrapper(ItemStack stack, FluidStack fluid) {
            this.stack = stack;
            this.fluid = fluid;
        }


        public ItemStack getStack() {
            return stack;
        }

        public FluidStack getFluid() {
            return fluid;
        }
    }

}
