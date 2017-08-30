package com.buuz135.industrial.api.recipe;


import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BioReactorEntry {

    public static List<BioReactorEntry> BIO_REACTOR_ENTRIES = new ArrayList<>();

    private ItemStack stack;
    private Predicate<NBTTagCompound> nbtCheck;

    /**
     * Defines an ItemStack that can be insterted into the Bioreactor.
     *
     * @param stack    The stack that can be insterted into the Bioreactor
     * @param nbtCheck A check for others ItemStack special NBTTag (Like Forestry Species)
     */
    public BioReactorEntry(ItemStack stack, @Nullable Predicate<NBTTagCompound> nbtCheck) {
        this.stack = stack;
        this.nbtCheck = nbtCheck;
    }

    /**
     * Defines an ItemStack that can be insterted into the Bioreactor.
     *
     * @param stack The stack that can be insterted into the Bioreactor
     */
    public BioReactorEntry(ItemStack stack) {
        this(stack, null);
    }

    /**
     * Checks if another ItemStack is the same ItemStack with NBT Checking as the original.
     *
     * @param stack The ItemStack to check
     * @return true if they match, false if they don't.
     */
    public boolean doesStackMatch(ItemStack stack) {
        return this.stack.isItemEqual(stack) && (this.nbtCheck == null || !stack.hasTagCompound() || this.nbtCheck.apply(stack.getTagCompound()));
    }

    public ItemStack getStack() {
        return stack;
    }

    public Predicate<NBTTagCompound> getNbtCheck() {
        return nbtCheck;
    }
}
