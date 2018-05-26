package com.buuz135.industrial.proxy.block.filter;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackFilter extends AbstractFilter<EntityItem> {

    public ItemStackFilter(int size) {
        super(size);
    }

    @Override
    public boolean acceptsInput(ItemStack stack) {
        return true;
    }

    @Override
    public boolean matches(EntityItem entity) { //TODO more depth comparing
        boolean isEmpty = true;
        for (ItemStack stack : this.filter) {
            if (!stack.isEmpty()) isEmpty = false;
        }
        if (isEmpty) return false;
        for (ItemStack stack : this.filter) {
            if (stack.isItemEqual(entity.getItem())) return true;
        }
        return false;
    }

    @Override
    public void setFilter(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.filter.length) this.filter[slot] = stack;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for (int i = 0; i < this.filter.length; i++) {
            if (!this.filter[i].isEmpty()) compound.setTag(String.valueOf(i), this.filter[i].serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (int i = 0; i < this.filter.length; i++) {
            if (nbt.hasKey(String.valueOf(i))) {
                this.filter[i] = new ItemStack(nbt.getCompoundTag(String.valueOf(i)));
            } else this.filter[i] = ItemStack.EMPTY;
        }
    }
}
