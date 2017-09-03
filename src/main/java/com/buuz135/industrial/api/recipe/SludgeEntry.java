package com.buuz135.industrial.api.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SludgeEntry {

    public static List<SludgeEntry> SLUDGE_RECIPES = new ArrayList<>();

    private ItemStack stack;
    private int weight;

    /**
     * Represents an entry for the sludge refiner.
     *
     * @param stack  The ItemStack as an output.
     * @param weight The weight in the global pool of items.
     */
    public SludgeEntry(ItemStack stack, int weight) {
        this.stack = stack;
        this.weight = weight;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getWeight() {
        return weight;
    }
}
