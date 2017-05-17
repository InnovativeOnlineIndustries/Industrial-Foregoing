package com.buuz135.industrial.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class ItemStackWeightedItem extends WeightedRandom.Item {

    private ItemStack stack;

    public ItemStackWeightedItem(ItemStack stack, int itemWeightIn) {
        super(itemWeightIn);
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }


}
