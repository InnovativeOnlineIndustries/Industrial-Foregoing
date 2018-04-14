package com.buuz135.industrial.utils.apihandlers.crafttweaker;

import com.buuz135.industrial.api.recipe.FluidDictionaryEntry;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.industrialforegoing.FluidDictionary")
public class CTFluidDictionary {

    @ZenMethod
    public static void add(String fluidInput, String fluidOutput, double ratio) {
        CraftTweakerAPI.apply(new Add(new FluidDictionaryEntry(fluidInput, fluidOutput, ratio)));
    }

    @ZenMethod
    public static void remove(String fluidInput, String fluidOutput) {
        CraftTweakerAPI.apply(new Remove(fluidInput, fluidOutput));
    }

    private static class Add implements IAction {

        private final FluidDictionaryEntry entry;

        private Add(FluidDictionaryEntry entry) {
            this.entry = entry;
        }


        @Override
        public void apply() {
            RecipeHandlers.FLUID_DICTIONARY_ENTRIES.put(CTAction.ADD, entry);
        }

        @Override
        public String describe() {
            return "Adding FluidDictionary Entry " + entry.getFluidOrigin() + " * " + entry.getRatio() + " = " + entry.getFluidResult();
        }
    }

    private static class Remove implements IAction {

        private final String origin;
        private final String result;

        private Remove(String origin, String result) {
            this.origin = origin;
            this.result = result;
        }

        @Override
        public void apply() {
            RecipeHandlers.FLUID_DICTIONARY_ENTRIES.put(CTAction.REMOVE, new FluidDictionaryEntry(origin, result, 0));
        }

        @Override
        public String describe() {
            return "Removing  FluidDictionary Entry " + origin + " " + result;
        }
    }
}
