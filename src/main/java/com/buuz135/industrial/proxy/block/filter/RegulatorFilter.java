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
    private IFilter.GhostSlot[] filter;

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

    public abstract int matches(TYPE stack, CAP cap, boolean isRegulated);

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

    public IFilter.GhostSlot[] getFilter() {
        return filter;
    }

    public void setFilter(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.getFilter().length) {
            this.getFilter()[slot].setStack(stack);
            this.getFilter()[slot].setAmount(stack.getCount());
        }
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compound = new CompoundTag();
        for (int i = 0; i < this.getFilter().length; i++) {
            if (!this.getFilter()[i].getStack().isEmpty()) {
                CompoundTag slot = new CompoundTag();
                slot.put("Stack", this.getFilter()[i].getStack().saveOptional(provider));
                slot.putInt("Amount", this.getFilter()[i].getAmount());
                compound.put(String.valueOf(i), slot);
            }

        }
        return compound;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        for (int i = 0; i < this.getFilter().length; i++) {
            if (nbt.contains(String.valueOf(i))) {
                CompoundTag slot = nbt.getCompound(String.valueOf(i));
                this.getFilter()[i].setStack(ItemStack.parseOptional(provider, slot.getCompound("Stack")));
                this.getFilter()[i].setAmount(slot.getInt("Amount"));
            } else {
                this.getFilter()[i].setStack(ItemStack.EMPTY);
            }
        }
    }

    public boolean isEmpty() {
        for (IFilter.GhostSlot slot : this.getFilter()) {
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
