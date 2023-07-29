/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class BLHBlockItemHandlerItemStack implements IItemHandler {

    public final ItemStack stack;
    public final int slotLimit;

    public BLHBlockItemHandlerItemStack(ItemStack stack, int slotLimit) {
        this.stack = stack;
        this.slotLimit = slotLimit;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemStack copied = getStack();
        copied.setCount(getAmount());
        return copied;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (isItemValid(slot, stack)) {
            int amount = getSlotLimit(slot);
            int stored = getAmount();
            int inserted = Math.min(amount - stored, stack.getCount());
            if (getVoid()) inserted = stack.getCount();
            if (!simulate) {
                setStack(stack);
                setAmount(Math.min(stored + inserted, amount));
            }
            if (inserted == stack.getCount()) return ItemStack.EMPTY;
            return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - inserted);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;
        ItemStack blStack = getStack();
        int stored = getAmount();
        if (blStack.isEmpty()) return ItemStack.EMPTY;
        if (stored <= amount) {
            ItemStack out = blStack.copy();
            int newAmount = stored;
            if (!simulate) {
                //setStack(ItemStack.EMPTY);
                setAmount(0);
            }
            out.setCount(newAmount);
            return out;
        } else {
            if (!simulate) {
                setAmount(stored - amount);

            }
            return ItemHandlerHelper.copyStackWithSize(blStack, amount);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return slotLimit;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        ItemStack current = getStack();
        return current.isEmpty() || ItemStack.isSameItemSameTags(current, stack);
    }

    public int getAmount() {
        CompoundTag tag = getTag();
        if (tag != null && tag.contains("stored")) {
            return tag.getInt("stored");
        }
        return 0;
    }

    public ItemStack getStack() {
        CompoundTag tag = getTag();
        if (tag != null && tag.contains("blStack")) {
            return ItemStack.of(tag.getCompound("blStack"));
        }
        return ItemStack.EMPTY;
    }

    public boolean getVoid() {
        CompoundTag tag = getTag();
        if (tag != null && tag.contains("voidItems")) {
            return tag.getBoolean("voidItems");
        }
        return true;
    }

    private void setAmount(int amount) {
        CompoundTag tag = getTag();
        if (tag == null) {
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.put("BlockEntityTag", new CompoundTag());
            this.stack.setTag(compoundNBT);
        }
        this.stack.getTag().getCompound("BlockEntityTag").putInt("stored", amount);
    }

    private void setStack(ItemStack stack) {
        CompoundTag tag = getTag();
        if (tag == null) {
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.put("BlockEntityTag", new CompoundTag());
            this.stack.setTag(compoundNBT);
        }
        this.stack.getTag().getCompound("BlockEntityTag").put("blStack", stack.serializeNBT());
    }

    private CompoundTag getTag() {
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
            return stack.getTag().getCompound("BlockEntityTag");
        return null;
    }


}
