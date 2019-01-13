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
package com.buuz135.industrial.jei.stonework;

import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.List;

public class StoneWorkWrapper implements IRecipeWrapper {

    private final List<MaterialStoneWorkFactoryTile.Mode> modes;
    private final ItemStack output;

    public StoneWorkWrapper(List<MaterialStoneWorkFactoryTile.Mode> modes, ItemStack output) {
        this.modes = modes;
        this.output = output;
        while (this.modes.size() < 4) {
            this.modes.add(MaterialStoneWorkFactoryTile.Mode.NONE);
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(Blocks.COBBLESTONE));
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        for (int i = 0; i < modes.size(); i++) {
            ItemStackUtils.renderItemIntoGUI(modes.get(i).getItemStack(), 29 + i * 24, 5, 7);
        }
    }

    public List<MaterialStoneWorkFactoryTile.Mode> getModes() {
        return modes;
    }

    public ItemStack getOutput() {
        return output;
    }
}
