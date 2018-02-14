package com.buuz135.industrial.book;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.LinkedHashMap;
import java.util.Map;

public enum BookCategory {
    GETTING_STARTED("getting_started", new ItemStack(ItemRegistry.plastic)),
    GENERATORS("generators", new ItemStack(BlockRegistry.petrifiedFuelGeneratorBlock)),
    AGRICULTURE("agriculture", new ItemStack(BlockRegistry.cropRecolectorBlock)),
    ANIMAL_HUSBANDRY("animal_husbandry", new ItemStack(Items.COOKED_BEEF)),
    MAGIC("magic", new ItemStack(BlockRegistry.potionEnervatorBlock)),
    STORAGE("storage", new ItemStack(BlockRegistry.blackHoleUnitBlock)),
    MOB("mob_interaction", new ItemStack(BlockRegistry.mobDetectorBlock)),
    RESOURCE_PRODUCTION("resource_production", new ItemStack(BlockRegistry.laserDrillBlock)),
    ITEM("items", new ItemStack(ItemRegistry.rangeAddonItem, 1, 11));

    private String name;
    private ItemStack display;
    private Map<ResourceLocation, CategoryEntry> entries;

    BookCategory(String name, ItemStack display) {
        this.name = new TextComponentTranslation("text.industrialforegoing.book.category." + name).getFormattedText();
        this.display = display;
        entries = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public ItemStack getDisplay() {
        return display;
    }

    public Map<ResourceLocation, CategoryEntry> getEntries() {
        return entries;
    }
}
