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
