/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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
package com.buuz135.industrial.proxy;

import com.buuz135.industrial.api.juicer.JuicerRecipe;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.apihandlers.juicer.SimpleJuicerRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;


public class JuicerRegistry {

    @SubscribeEvent
    public void register(RegistryEvent.Register<JuicerRecipe> event) {
        IForgeRegistry<JuicerRecipe> registry = event.getRegistry();
        registry.registerAll(
                new SimpleJuicerRecipe("apple", new OreIngredient("foodApple"), new FluidStack(FluidsRegistry.APPLE, 50)),
                new SimpleJuicerRecipe("orange", new OreIngredient("foodOrange"), new FluidStack(FluidsRegistry.ORANGE, 50))
        );
    }

    public static JuicerRecipe getRecipe(ItemStack stack) {
        for (JuicerRecipe recipe : IFRegistries.JUICER_RECIPE_REGISTRY.getValuesCollection()) {
            if (recipe.getIngredient().apply(stack))
                return recipe;
        }
        return null;
    }
}