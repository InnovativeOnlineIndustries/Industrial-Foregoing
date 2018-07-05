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
