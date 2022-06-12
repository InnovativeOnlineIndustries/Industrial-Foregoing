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
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

public class LaserLensItem extends IFCustomItem {

    private int color;

    public LaserLensItem(int color) {
        super("laser_lens" + color, ModuleCore.TAB_CORE, new Properties().stacksTo(1));
        this.color = color;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(Tags.Items.GLASS_PANES),
                        new Ingredient.TagValue(Tags.Items.GLASS_PANES),
                        new Ingredient.TagValue(Tags.Items.GLASS_PANES),
                        new Ingredient.TagValue(Tags.Items.GLASS_PANES),
                        new Ingredient.TagValue(DyeColor.byId(color).getTag()),
                },
                new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 250), 100, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TextComponent(new TranslatableComponent("color.minecraft." + DyeColor.byId(color).getName()).getString() +
                " " + new TranslatableComponent("item.industrialforegoing.laser_lens").getString());
    }
}
