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

package com.buuz135.industrial.item;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class LaserLensItem extends IFCustomItem {

    private DyeColor color;

    public LaserLensItem(DyeColor color) {
        super(color.getName() + "_laser_lens", ModuleCore.TAB_CORE, new Properties().stacksTo(1));
        this.color = color;
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        DissolutionChamberRecipe.createRecipe(consumer, color.getName() + "_laser_lens", new DissolutionChamberRecipe(List.of(
                Ingredient.of(Tags.Items.GLASS_PANES),
                Ingredient.of(Tags.Items.GLASS_PANES),
                Ingredient.of(Tags.Items.GLASS_PANES),
                Ingredient.of(Tags.Items.GLASS_PANES),
                Ingredient.of(color.getTag())
        ), new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 250), 100, Optional.of(new ItemStack(this)), Optional.empty()));

    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(Component.translatable("color.minecraft." + color.getName()).getString() +
                " " + Component.translatable("item.industrialforegoing.laser_lens").getString());
    }
}
