package com.buuz135.industrial.api.book;

import net.minecraft.item.ItemStack;

import java.util.List;

public class CategoryEntry {

    private String name;
    private ItemStack display;
    private List<IPage> pages;

    public CategoryEntry(String name, ItemStack display, List<IPage> pages) {
        this.name = name;
        this.display = display;
        this.pages = pages;
    }

    public String getName() {
        return name;
    }

    public ItemStack getDisplay() {
        return display;
    }

    public List<IPage> getPages() {
        return pages;
    }
}
