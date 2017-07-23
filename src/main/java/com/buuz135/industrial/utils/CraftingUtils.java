package com.buuz135.industrial.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class CraftingUtils {

    private static HashMap<ItemStack, ItemStack> crushedRecipes = new HashMap<>();

    public static ItemStack findOutput(int size, ItemStack input, World world) {
        InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, size, size);
        for (int i = 0; i < size * size; ++i) {
            inventoryCrafting.setInventorySlotContents(i, input.copy());
        }
        return CraftingManager.findMatchingResult(inventoryCrafting, world);
    }

    public static ItemStack getCrushOutput(ItemStack stack) {
        for (Map.Entry<ItemStack, ItemStack> entry : crushedRecipes.entrySet()) {
            if (entry.getKey().isItemEqual(stack)) {
                return entry.getValue();
            }
        }
        return ItemStack.EMPTY;
    }

    public static void generateCrushedRecipes() {
        crushedRecipes.put(new ItemStack(Blocks.STONE), new ItemStack(Blocks.COBBLESTONE));
        crushedRecipes.put(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL));
        crushedRecipes.put(new ItemStack(Blocks.GRAVEL), new ItemStack(Blocks.SAND));
        NonNullList<ItemStack> items = OreDictionary.getOres("itemSilicon");
        if (items.size() > 0) crushedRecipes.put(new ItemStack(Blocks.SAND), items.get(0));
    }
}
