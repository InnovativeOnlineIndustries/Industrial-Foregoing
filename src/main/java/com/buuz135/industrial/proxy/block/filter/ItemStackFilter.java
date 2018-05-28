package com.buuz135.industrial.proxy.block.filter;

import com.buuz135.industrial.proxy.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackFilter extends AbstractFilter<Entity> {

    public ItemStackFilter(int locX, int locY, int sizeX, int sizeY) {
        super(locX, locY, sizeX, sizeY);
    }

    @Override
    public boolean acceptsInput(ItemStack stack) {
        return true;
    }

    @Override
    public boolean matches(Entity entity) {
        boolean isEmpty = true;
        for (GhostSlot stack : this.getFilter()) {
            if (!stack.getStack().isEmpty()) isEmpty = false;
        }
        if (isEmpty) return false;
        for (GhostSlot stack : this.getFilter()) {
            if (entity instanceof EntityItem && stack.getStack().isItemEqual(((EntityItem) entity).getItem()))
                return true;
            if (entity instanceof EntityLiving && stack.getStack().getItem().equals(ItemRegistry.mobImprisonmentToolItem) && ItemRegistry.mobImprisonmentToolItem.containsEntity(stack.getStack())
                    && EntityList.getKey(entity).toString().equalsIgnoreCase(ItemRegistry.mobImprisonmentToolItem.getID(stack.getStack()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setFilter(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.getFilter().length) this.getFilter()[slot].setStack(stack);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for (int i = 0; i < this.getFilter().length; i++) {
            if (!this.getFilter()[i].getStack().isEmpty())
                compound.setTag(String.valueOf(i), this.getFilter()[i].getStack().serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (int i = 0; i < this.getFilter().length; i++) {
            if (nbt.hasKey(String.valueOf(i))) {
                this.getFilter()[i].setStack(new ItemStack(nbt.getCompoundTag(String.valueOf(i))));
            } else this.getFilter()[i].setStack(ItemStack.EMPTY);
        }
    }
}
