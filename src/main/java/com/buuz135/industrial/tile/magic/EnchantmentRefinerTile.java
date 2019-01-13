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

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class EnchantmentRefinerTile extends CustomElectricMachine {

    private ItemStackHandler input;
    private ItemStackHandler outputNoEnch;
    private ItemStackHandler outputEnch;


    public EnchantmentRefinerTile() {
        super(EnchantmentRefinerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.initInputInv();
        this.initOutputInv();
    }

    private void initInputInv() {
        input = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentRefinerTile.this.markDirty();
            }
        };

        this.addInventory(new CustomColoredItemHandler(this.input, EnumDyeColor.GREEN, "Input items", 18 * 3, 25, 1, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(this.input, "ench_ref_in");

    }

    private void initOutputInv() {
        outputEnch = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentRefinerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outputEnch, EnumDyeColor.PURPLE, "Enchanted Items", 18 * 4 + 14, 25, 4, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outputEnch, "ench_ref_out_yes");

        outputNoEnch = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentRefinerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outputNoEnch, EnumDyeColor.YELLOW, "No enchanted Items", 18 * 4 + 14, 25 + 18 * 2, 4, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outputNoEnch, "ench_ref_out_no");
    }

    public ItemStack getFirstItem() {
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!input.getStackInSlot(i).isEmpty()) {
                return input.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        ItemStack stack = getFirstItem();
        if (stack.isEmpty()) {
            return 0;
        }
        ItemStack out = stack.copy();
        IItemHandler handler = (stack.isItemEnchanted() || stack.getItem().equals(Items.ENCHANTED_BOOK)) ? outputEnch : outputNoEnch;
        if (ItemHandlerHelper.insertItem(handler, out, true).isEmpty()) {
            ItemHandlerHelper.insertItem(handler, out, false);
            stack.setCount(0);
            return 1;
        }
        return 0;
    }

}
