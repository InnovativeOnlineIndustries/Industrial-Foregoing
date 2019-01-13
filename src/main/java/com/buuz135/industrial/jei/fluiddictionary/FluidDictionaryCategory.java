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
package com.buuz135.industrial.jei.fluiddictionary;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidDictionaryCategory implements IRecipeCategory<FluidDictionaryWrapper> {

    private final IGuiHelper guiHelper;
    private IDrawable tankOverlay;

    public FluidDictionaryCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        tankOverlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public String getUid() {
        return "fluid_dictionary";
    }

    @Override
    public String getTitle() {
        return "Fluid Dictionary Conversion";
    }

    @Override
    public String getModName() {
        return Reference.MOD_ID;
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 129, 70, 50);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FluidDictionaryWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        fluidStackGroup.init(0, true, 5, 1, 12, 48, 1000, false, tankOverlay);
        fluidStackGroup.init(1, false, 57, 1, 12, 48, 1000, false, tankOverlay);

        fluidStackGroup.set(0, ingredients.getInputs(FluidStack.class).get(0));
        fluidStackGroup.set(1, ingredients.getOutputs(FluidStack.class).get(0));
    }
}
