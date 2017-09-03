package com.buuz135.industrial.utils.apihandlers.crafttweaker;

import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.industrialforegoing.LaserDrill")
public class CTLaserDrill {

    @ZenMethod
    public static void add(int meta, IItemStack output, int weight) {
        LaserDrillEntry entry = new LaserDrillEntry(meta, (ItemStack) output.getInternal(), weight);
        CraftTweakerAPI.apply(new Add(entry));
    }

    @ZenMethod
    public static void remove(IItemStack input) {
        CraftTweakerAPI.apply(new Remove((ItemStack) input.getInternal()));
    }

    private static class Add implements IAction {

        private final LaserDrillEntry entry;

        private Add(LaserDrillEntry entry) {
            this.entry = entry;
        }


        @Override
        public void apply() {
            RecipeHandlers.LASER_ENTRIES.put(CTAction.ADD, entry);
        }

        @Override
        public String describe() {
            return "Adding Laser drill " + entry.getStack().getDisplayName();
        }
    }

    private static class Remove implements IAction {

        private final ItemStack stack;

        private Remove(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            RecipeHandlers.LASER_ENTRIES.put(CTAction.REMOVE, new LaserDrillEntry(0, stack, 0));
        }

        @Override
        public String describe() {
            return "Removing Laser Drill entry " + stack.getDisplayName();
        }
    }
}
