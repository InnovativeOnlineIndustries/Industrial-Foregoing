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
package com.buuz135.industrial.utils.apihandlers.crafttweaker;

import com.buuz135.industrial.api.extractor.ExtractorEntry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.industrialforegoing.Extractor")
public class CTExtractor {

    @ZenMethod
    public static void add(IItemStack input, ILiquidStack stack) {
        ExtractorEntry extractorEntry = new ExtractorEntry((ItemStack) input.getInternal(), (FluidStack) stack.getInternal());
        CraftTweakerAPI.apply(new Add(extractorEntry));
    }

    @ZenMethod
    public static void add(IItemStack input, ILiquidStack stack, float breakChance) {
        ExtractorEntry extractorEntry = new ExtractorEntry((ItemStack) input.getInternal(), (FluidStack) stack.getInternal(), breakChance);
        CraftTweakerAPI.apply(new Add(extractorEntry));
    }

    @ZenMethod
    public static void remove(IItemStack input) {
        CraftTweakerAPI.apply(new Remove((ItemStack) input.getInternal()));
    }

    private static class Add implements IAction {

        private final ExtractorEntry entry;

        private Add(ExtractorEntry entry) {
            this.entry = entry;
        }

        @Override
        public void apply() {
            RecipeHandlers.EXTRACTOR_ENTRIES.put(CTAction.ADD, entry);
        }

        @Override
        public String describe() {
            return "Adding Extractor enties" + entry.getItemStack().getDisplayName();
        }
    }

    private static class Remove implements IAction {

        private final ItemStack stack;

        private Remove(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            RecipeHandlers.EXTRACTOR_ENTRIES.put(CTAction.REMOVE, new ExtractorEntry(stack, new FluidStack(FluidsRegistry.LATEX, 1)));
        }

        @Override
        public String describe() {
            return "Removing Extractor " + stack.getDisplayName();
        }
    }
}
