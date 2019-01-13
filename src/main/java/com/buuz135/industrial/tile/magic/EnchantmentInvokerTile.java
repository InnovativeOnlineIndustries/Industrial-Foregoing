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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class EnchantmentInvokerTile extends CustomElectricMachine {

    private IFluidTank essenceTank;
    private ItemStackHandler input;
    private ItemStackHandler output;

    public EnchantmentInvokerTile() {
        super(EnchantmentInvokerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentInvokerTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new CustomColoredItemHandler(input, EnumDyeColor.BLUE, "Input items", 18 * 2 + 13, 25, 1, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().getItemEnchantability(stack) > 0 && (stack.getItem().equals(Items.BOOK) || stack.isItemEnchantable());
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        this.addInventoryToStorage(input, "input");
        essenceTank = this.addFluidTank(FluidsRegistry.ESSENCE, 32000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(18 * 4, 25, 18, 54));
        output = new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentInvokerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(output, EnumDyeColor.ORANGE, "Output items", 18 * 6 + 5, 25, 3, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(output, "output");
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;//enchantment_invoker

        ItemStack stack = getFirstItem();
        if (essenceTank.getFluidAmount() >= 3000 && !stack.isEmpty() && ItemHandlerHelper.insertItem(output, stack, true).isEmpty()) {
            essenceTank.drain(3000, true);
            ItemHandlerHelper.insertItem(output, EnchantmentHelper.addRandomEnchantment(this.world.rand, stack.copy(), 30, true), false);
            stack.setCount(0);
            return 1;
        }
        return 0;
    }

    private ItemStack getFirstItem() {
        for (int i = 0; i < input.getSlots(); ++i)
            if (!input.getStackInSlot(i).isEmpty()) return input.getStackInSlot(i);
        return ItemStack.EMPTY;
    }
}
