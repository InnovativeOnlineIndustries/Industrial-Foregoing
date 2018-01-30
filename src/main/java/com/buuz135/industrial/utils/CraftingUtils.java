package com.buuz135.industrial.utils;

import com.buuz135.industrial.proxy.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class CraftingUtils {

    private static HashMap<ItemStack, ItemStack> crushedRecipes = new HashMap<>();
    private static HashMap<ItemStack, ItemStack> cachedRecipes = new HashMap<>();

    public static ItemStack findOutput(int size, ItemStack input, World world) {
        ItemStack cachedStack = input.copy();
        cachedStack.setCount(size * size);
        for (Map.Entry<ItemStack, ItemStack> entry : cachedRecipes.entrySet()) {
            if (entry.getKey().isItemEqual(cachedStack)) {
                return entry.getValue().copy();
            }
        }
        InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, size, size);
        for (int i = 0; i < size * size; i++) {
            inventoryCrafting.setInventorySlotContents(i, input.copy());
        }
        ItemStack output = CraftingManager.findMatchingResult(inventoryCrafting, world);
        cachedRecipes.put(cachedStack, output.copy());
        return output.copy();
    }


    public static InventoryCrafting genCraftingInventory(World world, ItemStack... inputs) {
        InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < 9; ++i) {
            inventoryCrafting.setInventorySlotContents(i, inputs[i]);
        }
        return inventoryCrafting;
    }

    public static IRecipe findRecipe(World world, ItemStack... inputs) {
        return CraftingManager.findMatchingRecipe(genCraftingInventory(world, inputs), world);
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
        ItemStack latest = new ItemStack(Blocks.SAND);
        if (BlockRegistry.materialStoneWorkFactoryBlock.produceExNihiloDust() && Loader.isModLoaded("exnihilocreatio")) {
            Block dust = Block.REGISTRY.getObject(new ResourceLocation("exnihilocreatio:block_dust"));
            crushedRecipes.put(new ItemStack(Blocks.SAND), latest = new ItemStack(dust));
        }
        if (BlockRegistry.materialStoneWorkFactoryBlock.produceSilicon()) {
            NonNullList<ItemStack> items = OreDictionary.getOres("itemSilicon");
            if (items.size() > 0) crushedRecipes.put(latest, items.get(0));
        }
    }
}
