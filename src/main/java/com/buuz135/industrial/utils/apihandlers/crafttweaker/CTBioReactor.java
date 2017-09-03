package com.buuz135.industrial.utils.apihandlers.crafttweaker;

import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.google.common.collect.LinkedListMultimap;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.industrialforegoing.BioReactor")
public class CTBioReactor {

    public static final LinkedListMultimap<CTAction, BioReactorEntry> ENTRIES = LinkedListMultimap.create();

    @ZenMethod
    public static void add(IItemStack input) {
        BioReactorEntry entry = new BioReactorEntry((ItemStack) input.getInternal());
        CraftTweakerAPI.apply(new Add(entry));
    }

    @ZenMethod
    public static void remove(IItemStack input) {
        CraftTweakerAPI.apply(new Remove((ItemStack) input.getInternal()));
    }

    private static class Add implements IAction {

        private final BioReactorEntry entry;

        private Add(BioReactorEntry entry) {
            this.entry = entry;
        }


        @Override
        public void apply() {
            ENTRIES.put(CTAction.ADD, entry);
        }

        @Override
        public String describe() {
            return "Adding BioReactor Entry " + entry.getStack().getDisplayName();
        }
    }

    private static class Remove implements IAction {

        private final ItemStack stack;

        private Remove(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            ENTRIES.put(CTAction.REMOVE, new BioReactorEntry(stack));
        }

        @Override
        public String describe() {
            return "Removing BioReactor Entry " + stack.getDisplayName();
        }
    }
}
