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
package com.buuz135.industrial.proxy.block.filter;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class RegulatorFilter<TYPE, CAP> {

    private final int sizeX;
    private final int sizeY;
    private final int locX;
    private final int locY;
    private final int smallMultiplier;
    private final int bigMultiplier;
    private final int maxAmount;
    private final String label;
    private final IFilter.GhostSlot[] filter;
    private boolean whitelisted = false;
    private boolean isRegulated = false;

    public RegulatorFilter(int locX, int locY, int sizeX, int sizeY, int smallMultiplier, int bigMultiplier, int maxAmount, String label) {
        this.locX = locX;
        this.locY = locY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.smallMultiplier = smallMultiplier;
        this.bigMultiplier = bigMultiplier;
        this.maxAmount = maxAmount;
        this.label = label;
        this.filter = new IFilter.GhostSlot[sizeX * sizeY];
        int pos = 0;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                this.filter[pos] = new IFilter.GhostSlot(pos, locX + (x * 18), locY + y * 18);
                this.filter[pos].setMaxAmount(this.maxAmount);
                ++pos;
            }
        }
    }

    /**
     * Used to check if provided TYPE matches the filter. This method ignores regulation rule.
     * @param stack TYPE to check
     * @param cap Capability of this filter.
     * @return True if provided TYPE matches, false otherwise.
     */
    public abstract boolean matches(TYPE stack, CAP cap);

    /**
     * Used to get the amount to extract of the provided TYPE.
     * @param stack TYPE to check.
     * @param cap Capability of this filter.
     * @return Amount to extract, complying to the regulation rule.
     */
    public abstract int getExtractAmount(TYPE stack, CAP cap);

    /**
     * Used to get the possible amount of TYPE to insert.
     * @param stack TYPE to check.
     * @param cap Capability of this filter.
     * @return Possible amount to insert, complying to the regulation rule.
     */
    public abstract int getInsertAmount(TYPE stack, CAP cap);

    /**
     * Used to get the amount of the item set in the filter. Checks all slots of the filter.
     * @param stack TYPE to look for.
     * @return Amount of the TYPE set in the filter.
     * @apiNote If not Regulated, will return either 0 or 1.
     */
    public abstract int getFilterAmount(TYPE stack);

    /**
     * Used to get the amount of the item in the provided IItemHandler.
     * @param stack TYPE to look for.
     * @return Amount of the TYPE in the storage.
     */
    public abstract int getStorageAmount(TYPE stack, CAP cap);

    public boolean canExtract(TYPE stack, CAP cap) {
        return this.getExtractAmount(stack, cap) > 0;
    }

    public boolean canInsert(TYPE stack, CAP cap) {
        return this.getInsertAmount(stack, cap) > 0;
    }

    public boolean isRegulated() {
        return isRegulated;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public void setRegulated(boolean regulated) {
        isRegulated = regulated;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getLocX() {
        return locX;
    }

    public int getLocY() {
        return locY;
    }

    public IFilter.GhostSlot[] getFilterSlots() {
        return filter;
    }

    public void setFilter(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.getFilterSlots().length) {
            this.getFilterSlots()[slot].setStack(stack);
            this.getFilterSlots()[slot].setAmount(stack.getCount());
        }
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("Whitelist", whitelisted);
        compound.putBoolean("Regulated", isRegulated);
        for (int i = 0; i < this.getFilterSlots().length; i++) {
            if (!this.getFilterSlots()[i].getStack().isEmpty()) {
                CompoundTag slot = new CompoundTag();
                slot.put("Stack", this.getFilterSlots()[i].getStack().saveOptional(provider));
                slot.putInt("Amount", this.getFilterSlots()[i].getAmount());
                compound.put(String.valueOf(i), slot);
            }
        }
        return compound;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        whitelisted = nbt.getBoolean("Whitelist");
        isRegulated = nbt.getBoolean("Regulated");
        for (int i = 0; i < this.getFilterSlots().length; i++) {
            if (nbt.contains(String.valueOf(i))) {
                CompoundTag slot = nbt.getCompound(String.valueOf(i));
                this.getFilterSlots()[i].setStack(ItemStack.parseOptional(provider, slot.getCompound("Stack")));
                this.getFilterSlots()[i].setAmount(slot.getInt("Amount"));
            } else {
                this.getFilterSlots()[i].setStack(ItemStack.EMPTY);
            }
        }
    }

    public boolean isEmpty() {
        for (IFilter.GhostSlot slot : this.getFilterSlots()) {
            if (!slot.getStack().isEmpty()) return false;
        }
        return true;
    }

    public int getSmallMultiplier() {
        return smallMultiplier;
    }

    public int getBigMultiplier() {
        return bigMultiplier;
    }

    public String getLabel() {
        return label;
    }
}
