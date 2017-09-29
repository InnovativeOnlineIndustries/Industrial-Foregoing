package com.buuz135.industrial.book;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public enum BookCategory {
    GETTING_STARTED("Getting Started", new ItemStack(ItemRegistry.plastic)),
    GENERATORS("Generators", new ItemStack(BlockRegistry.petrifiedFuelGeneratorBlock)),
    AGRICULTURE("Agriculture", new ItemStack(BlockRegistry.cropRecolectorBlock)),
    ANIMAL_HUSBANDRY("Animal Husbandry", new ItemStack(Items.COOKED_BEEF)),
    MAGIC("Magic", new ItemStack(BlockRegistry.potionEnervatorBlock)),
    STORAGE("Storage", new ItemStack(BlockRegistry.blackHoleUnitBlock)),
    MOB("Mob Interaction", new ItemStack(BlockRegistry.mobDetectorBlock)),
    RESOURCE_PRODUCTION("Resource Production", new ItemStack(BlockRegistry.laserDrillBlock)),
    ITEM("Items", new ItemStack(ItemRegistry.rangeAddonItem, 1, 11));

    private String name;
    private ItemStack display;
    private Map<ResourceLocation, CategoryEntry> entries;

    BookCategory(String name, ItemStack display) {
        this.name = name;
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
