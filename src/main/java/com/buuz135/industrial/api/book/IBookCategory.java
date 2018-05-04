package com.buuz135.industrial.api.book;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IBookCategory {

    public String getName();

    public ItemStack getDisplay();

    public Map<ResourceLocation, CategoryEntry> getEntries();

}
