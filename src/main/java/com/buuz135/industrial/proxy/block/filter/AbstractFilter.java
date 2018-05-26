package com.buuz135.industrial.proxy.block.filter;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public abstract class AbstractFilter<T extends Entity> implements IFilter<T> {

    protected ItemStack[] filter;

    public AbstractFilter(int size) {
        filter = new ItemStack[size];
        Arrays.fill(filter, ItemStack.EMPTY);
    }

    @Override
    public ItemStack[] getFilter() {
        return filter;
    }
}
