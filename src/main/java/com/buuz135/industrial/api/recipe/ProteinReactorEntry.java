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
