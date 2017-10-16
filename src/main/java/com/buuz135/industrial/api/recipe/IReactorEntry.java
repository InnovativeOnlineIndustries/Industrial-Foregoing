package com.buuz135.industrial.api.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Predicate;

public interface IReactorEntry {

    public boolean doesStackMatch(ItemStack stack);

    public ItemStack getStack();

    public Predicate<NBTTagCompound> getNbtCheck();
}
