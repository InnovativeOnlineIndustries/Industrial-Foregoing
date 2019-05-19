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
package com.buuz135.industrial.jei.machineproduce;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class MachineProduceCategory implements IRecipeCategory<MachineProduceWrapper> {
    private IGuiHelper guiHelper;

    public MachineProduceCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return null;
    }

    @Override
    public Class<? extends MachineProduceWrapper> getRecipeClass() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Machine Outputs";
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0, 82, 26);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(MachineProduceWrapper machineProduceWrapper, IIngredients iIngredients) {

    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MachineProduceWrapper recipeWrapper, IIngredients ingredients) {
        // IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        // guiItemStackGroup.init(0, true, 0, 4);
        // guiItemStackGroup.init(1, false, 60, 4);
        // guiItemStackGroup.set(0, ingredients.getInputs(ItemStack.class).get(0));
        // guiItemStackGroup.set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }

}
