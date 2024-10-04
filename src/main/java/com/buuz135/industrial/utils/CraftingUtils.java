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
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftingUtils {

    private static final Map<Integer, List<CraftingRecipe>> recipesBySize = new Int2ObjectArrayMap<>();

    public static ItemStack findOutput(int size, ItemStack input, Level world) {
        if (input.getCount() < size * size) return ItemStack.EMPTY;
        var recipe = getRecipeFor(size, input, world);
        if (recipe != null) {
            ItemStack output = recipe.getResultItem(world.registryAccess());
            return output.copy();
        }
        return ItemStack.EMPTY;
    }

    private static List<CraftingRecipe> getRecipesWithSize(int size, Level world) {
        return recipesBySize.computeIfAbsent(size, k -> {
            RecipeManager recipeManager = world.getRecipeManager();
            return recipeManager.getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> canCraftInDimensions(recipe, size) && allIngredientsEqual(recipe))
                .toList();
        });
    }

    private static boolean canCraftInDimensions(CraftingRecipe recipe, int size) {
        if (!recipe.canCraftInDimensions(size, size)) {
            return false;
        }
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return shapedRecipe.getWidth() == size && shapedRecipe.getHeight() == size;
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            return shapelessRecipe.getIngredients().size() == (size * size);
        }
        return true;
    }

    private static boolean allIngredientsEqual(CraftingRecipe recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int count = ingredients.size();
        if (count <= 1) {
            return true;
        }
        Ingredient firstIngredient = ingredients.getFirst();
        for (int i = 1; i < count; i++) {
            Ingredient ingredient = ingredients.get(i);
            if (!firstIngredient.equals(ingredient)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static CraftingRecipe getRecipeFor(int size, ItemStack input, Level world) {
        List<CraftingRecipe> craftingRecipes = getRecipesWithSize(size, world);

        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < size * size; i++) {
            inputs.add(input.copy());
        }
        CraftingInput craftingInput = CraftingInput.of(size, size, inputs);

        for (CraftingRecipe recipe : craftingRecipes) {
            if (recipe.matches(craftingInput, world)) {
                return recipe;
            }
        }
        return null;
    }

    public static ItemStack getCrushOutput(Level world, ItemStack stack) {
        for (CrusherRecipe recipe : RecipeUtil.getRecipes(world, (RecipeType<CrusherRecipe>)ModuleCore.CRUSHER_TYPE.get())) {
            if (recipe.input.test(stack)) return recipe.output.getItems()[0];
        }
        return ItemStack.EMPTY;
    }

}
