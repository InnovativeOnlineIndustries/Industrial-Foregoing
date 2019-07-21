/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.magic;


import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.Map;

public class EnchantmentAplicatorTile extends CustomElectricMachine {

    private ItemStackHandler inEnchantedBook;
    private ItemStackHandler inItem;
    private ItemStackHandler outEnchantedItem;
    private IFluidTank experienceTank;

    public EnchantmentAplicatorTile() {
        super(EnchantmentAplicatorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        this.initInputInv();
        this.initOutputInv();
    }

    private void initInputInv() {
        this.experienceTank = this.addFluidTank(FluidsRegistry.ESSENCE, 32000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(50, 25, 18, 54));

        inEnchantedBook = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(this.inEnchantedBook, EnumDyeColor.PURPLE, "Input enchanted books", 18 * 5 + 10, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.ENCHANTED_BOOK);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

        });
        this.addInventoryToStorage(this.inEnchantedBook, "ench_aplic_in_book");

        inItem = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new CustomColoredItemHandler(this.inItem, EnumDyeColor.GREEN, "Input items", 18 * 7 + 10, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return (stack.getItem().getItemEnchantability(stack) > 0 && (stack.isItemEnchanted() || stack.isItemEnchantable())) || stack.getItem().equals(Items.ENCHANTED_BOOK) || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemElytra;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(this.inItem, "ench_aplic_in_item");

    }

    private void initOutputInv() {
        outEnchantedItem = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outEnchantedItem, EnumDyeColor.MAGENTA, "Enchanted items", 18 * 5 + 10, 25 + 18 * 2, 3, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outEnchantedItem, "ench_aplic_out_item");

    }

    public double getLevels() {
        ItemStack itemstack1 = inItem.getStackInSlot(0);
        ItemStack itemstack2 = inEnchantedBook.getStackInSlot(0);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
        Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
        int amount = 0;
        for (Enchantment enchantment1 : map1.keySet()) {
            if (enchantment1 != null) {
                int firstEnchantmentLevel = map.getOrDefault(enchantment1, 0);
                int secondEnchantmentLevel = map1.get(enchantment1);
                secondEnchantmentLevel = firstEnchantmentLevel == secondEnchantmentLevel ? secondEnchantmentLevel + 1 : Math.max(secondEnchantmentLevel, firstEnchantmentLevel);
                for (Enchantment enchantment : map.keySet()) {
                    if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                        ++amount;
                    }
                }
                if (secondEnchantmentLevel > enchantment1.getMaxLevel()) {
                    secondEnchantmentLevel = enchantment1.getMaxLevel();
                }
                map.put(enchantment1, secondEnchantmentLevel);
                int rarity = 0;
                switch (enchantment1.getRarity()) {
                    case COMMON:
                        rarity = 1;
                        break;
                    case UNCOMMON:
                        rarity = 2;
                        break;
                    case RARE:
                        rarity = 4;
                        break;
                    case VERY_RARE:
                        rarity = 8;
                }
                amount += rarity * secondEnchantmentLevel;
            }
        }
        return amount / 2;
    }

    public boolean canWork() {
        return !inItem.getStackInSlot(0).isEmpty() && !inEnchantedBook.getStackInSlot(0).isEmpty();
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (!canWork()) return 0;
        int xp = (int) (getLevels() * 200);
        if (experienceTank.getFluidAmount() >= xp && ItemHandlerHelper.insertItem(outEnchantedItem, inItem.getStackInSlot(0), true).isEmpty()) {
            Map<Enchantment, Integer> mapFirst = EnchantmentHelper.getEnchantments(inEnchantedBook.getStackInSlot(0));
            Map<Enchantment, Integer> mapSecond = EnchantmentHelper.getEnchantments(inItem.getStackInSlot(0));
            int incompatibleAmount = 0;
            ItemStack stack = inItem.getStackInSlot(0).copy();
            for (Enchantment enchantmentFirst : mapFirst.keySet()) {
                boolean isIncompatible = false;
                for (Enchantment enchantmentSecond : mapSecond.keySet()) {
                    if (enchantmentFirst.equals(enchantmentSecond)) continue;
                    if (!enchantmentFirst.isCompatibleWith(enchantmentSecond) || !enchantmentFirst.canApplyAtEnchantingTable(stack)) {
                        isIncompatible = true;
                        ++incompatibleAmount;
                        break;
                    }
                }
                if (isIncompatible) continue;
                if (enchantmentFirst != null) {
                    if (mapSecond.containsKey(enchantmentFirst) && mapFirst.get(enchantmentFirst).equals(mapSecond.get(enchantmentFirst)) && mapFirst.get(enchantmentFirst) >= enchantmentFirst.getMaxLevel())
                        return 0;
                    int value = mapSecond.containsKey(enchantmentFirst) && mapFirst.get(enchantmentFirst).equals(mapSecond.get(enchantmentFirst)) ? Math.min(mapFirst.get(enchantmentFirst) + 1, enchantmentFirst.getMaxLevel()) : mapFirst.get(enchantmentFirst);
                    if (mapSecond.replace(enchantmentFirst, value) == null) {
                        mapSecond.put(enchantmentFirst, value);
                    }
                }
            }
            if (incompatibleAmount == mapFirst.size()) return 0;
            EnchantmentHelper.setEnchantments(mapSecond, stack);
            ItemHandlerHelper.insertItem(outEnchantedItem, stack, false);
            inItem.getStackInSlot(0).setCount(0);
            inEnchantedBook.getStackInSlot(0).setCount(0);
            experienceTank.drain(xp, true);
            return 1;
        }
        return 0;
    }
}
