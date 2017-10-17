package com.buuz135.industrial.utils.apihandlers.crafttweaker;

import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.industrialforegoing.ProteinReactor")
public class CTProteinReactor {


    @ZenMethod
    public static void add(IItemStack input) {
        ProteinReactorEntry entry = new ProteinReactorEntry((ItemStack) input.getInternal());
        CraftTweakerAPI.apply(new Add(entry));
    }

    @ZenMethod
    public static void remove(IItemStack input) {
        CraftTweakerAPI.apply(new Remove((ItemStack) input.getInternal()));
    }

    private static class Add implements IAction {

        private final ProteinReactorEntry entry;

        private Add(ProteinReactorEntry entry) {
            this.entry = entry;
        }


        @Override
        public void apply() {
            RecipeHandlers.PROTEIN_REACTOR_ENTRIES.put(CTAction.ADD, entry);
        }

        @Override
        public String describe() {
            return "Adding Protein Reactor Entry " + entry.getStack().getDisplayName();
        }
    }

    private static class Remove implements IAction {

        private final ItemStack stack;

        private Remove(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            RecipeHandlers.PROTEIN_REACTOR_ENTRIES.put(CTAction.REMOVE, new ProteinReactorEntry(stack));
        }

        @Override
        public String describe() {
            return "Removing Protein Reactor Entry " + stack.getDisplayName();
        }
    }
}
