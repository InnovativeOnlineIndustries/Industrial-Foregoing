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
package com.buuz135.industrial.api.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ProteinReactorEntry implements IReactorEntry {

    public static List<IReactorEntry> PROTEIN_REACTOR_ENTRIES = new ArrayList<>();

    private ItemStack stack;
    private Predicate<NBTTagCompound> nbtCheck;

    /**
     * Defines an ItemStack that can be insterted into the Proteinreactor.
     *
     * @param stack    The stack that can be insterted into the Proteinreactor.
     * @param nbtCheck A check for others ItemStack special NBTTag
     */
    public ProteinReactorEntry(ItemStack stack, @Nullable Predicate<NBTTagCompound> nbtCheck) {
        this.stack = stack;
        this.nbtCheck = nbtCheck;
    }

    /**
     * Defines an ItemStack that can be insterted into the Proteinreactor.
     *
     * @param stack The stack that can be insterted into the Proteinreactor
     */
    public ProteinReactorEntry(ItemStack stack) {
        this(stack, null);
    }

    /**
     * Checks if another ItemStack is the same ItemStack with NBT Checking as the original.
     *
     * @param stack The ItemStack to check
     * @return true if they match, false if they don't.
     */
    public boolean doesStackMatch(ItemStack stack) {
        return this.stack.isItemEqual(stack) && (this.nbtCheck == null || !stack.hasTagCompound() || this.nbtCheck.test(stack.getTagCompound()));
    }

    public ItemStack getStack() {
        return stack;
    }

    public Predicate<NBTTagCompound> getNbtCheck() {
        return nbtCheck;
    }
}
