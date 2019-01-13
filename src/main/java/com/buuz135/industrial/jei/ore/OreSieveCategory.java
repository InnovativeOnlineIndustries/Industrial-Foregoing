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
package com.buuz135.industrial.jei.ore;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class OreSieveCategory implements IRecipeCategory<OreSieveWrapper> {

    public final IGuiHelper helper;
    private IDrawable tankOverlay;

    public OreSieveCategory(IGuiHelper helper) {
        this.helper = helper;
        tankOverlay = helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public String getUid() {
        return "ORE_SIEVE";
    }

    @Override
    public String getTitle() {
        return BlockRegistry.oreSieveBlock.getLocalizedName();
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 4, 78, 77, 50);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OreSieveWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getFluidStacks().init(0, true, 1, 1, 12, 48, 300, true, tankOverlay);
        recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));

        recipeLayout.getItemStacks().init(1, false, 55, 17);
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));

        recipeLayout.getItemStacks().init(2, true, 25, 33);
        recipeLayout.getItemStacks().set(2, ingredients.getInputs(ItemStack.class).get(0));
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        Minecraft.getMinecraft().currentScreen.drawTexturedModalRect(25, 33, 0, 4, 18, 18);
    }
}
