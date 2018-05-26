package com.buuz135.industrial.proxy.block.filter;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public interface IFilter<T extends Entity> {

    boolean acceptsInput(ItemStack stack);

    boolean matches(T entity);

    void setFilter(int slot, ItemStack stack);

    ItemStack[] getFilter();

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound nbt);

}
