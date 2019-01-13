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

import com.buuz135.industrial.api.recipe.SludgeEntry;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.industrialforegoing.SludgeRefiner")
public class CTSludgeRefiner {

    @ZenMethod
    public static void add(IItemStack output, int weight) {
        SludgeEntry entry = new SludgeEntry((ItemStack) output.getInternal(), weight);
        CraftTweakerAPI.apply(new Add(entry));
    }

    @ZenMethod
    public static void remove(IItemStack output) {
        CraftTweakerAPI.apply(new Remove((ItemStack) output.getInternal()));
    }

    private static class Add implements IAction {

        private final SludgeEntry entry;

        private Add(SludgeEntry entry) {
            this.entry = entry;
        }

        @Override
        public void apply() {
            RecipeHandlers.SLUDGE_ENTRIES.put(CTAction.ADD, entry);
        }

        @Override
        public String describe() {
            return "Adding Sludge Refiner " + entry.getStack().getDisplayName();
        }
    }

    private static class Remove implements IAction {

        private final ItemStack stack;

        private Remove(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            RecipeHandlers.SLUDGE_ENTRIES.put(CTAction.REMOVE, new SludgeEntry(stack, 0));
        }

        @Override
        public String describe() {
            return "Removing Sludge Refiner " + stack.getDisplayName();
        }
    }
}
