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

package com.buuz135.industrial.utils;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.CrusherRecipe;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public class CraftingUtils {

    public static Set<ItemStack[]> missingRecipes = new HashSet<>();
    private static HashMap<ItemStack, ItemStack> cachedRecipes = new HashMap<>();

    public static ItemStack findOutput(int size, ItemStack input, Level world) {
        if (input.getCount() < size * size) return ItemStack.EMPTY;
        ItemStack cachedStack = input.copy();
        cachedStack.setCount(size * size);
        for (Map.Entry<ItemStack, ItemStack> entry : cachedRecipes.entrySet()) {
            if (ItemStack.isSameItem(entry.getValue(), cachedStack) && entry.getKey().getCount() == cachedStack.getCount()) {
                return entry.getValue().copy();
            }
        }
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < size * size; i++) {
            inputs.add(input.copy());
        }
        var recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(size, size, inputs), world).orElse(null);
        if (recipe != null) {
            ItemStack output = recipe.value().getResultItem(world.registryAccess());
            cachedRecipes.put(cachedStack, output.copy());
            return output.copy();
        }
        return ItemStack.EMPTY;
    }

    public static CraftingInput genCraftingInventory(Level world, ItemStack... inputs) {
        return CraftingInput.of(3, 3, Arrays.stream(inputs).toList());
    }

    public static Recipe findRecipe(Level world, ItemStack... inputs) {
        for (ItemStack[] missingRecipe : missingRecipes) {
            if (doesStackArrayEquals(missingRecipe, inputs)) return null;
        }
        RecipeHolder<CraftingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, genCraftingInventory(world, inputs), world).orElseGet(null);
        if (recipe == null) missingRecipes.add(inputs);
        return recipe.value();
    }

    public static boolean doesStackArrayEquals(ItemStack[] original, ItemStack[] compare) {
        if (original.length != compare.length) return false;
        for (int i = 0; i < original.length; i++) {
            if (original[i].isEmpty() && compare[i].isEmpty()) continue;
            if (!ItemStack.isSameItem(original[i], compare[i])) return false;
        }
        return true;
    }

    public static ItemStack getCrushOutput(Level world, ItemStack stack) {
        for (CrusherRecipe recipe : RecipeUtil.getRecipes(world, (RecipeType<CrusherRecipe>)ModuleCore.CRUSHER_TYPE.get())) {
            if (recipe.input.test(stack)) return recipe.output.getItems()[0];
        }
        return ItemStack.EMPTY;
    }

}
