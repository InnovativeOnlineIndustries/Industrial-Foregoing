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

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.module.ModuleTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

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
            if (entity instanceof ItemEntity && stack.getStack().sameItem(((ItemEntity) entity).getItem()))
                return true;
            if (entity instanceof LivingEntity && stack.getStack().getItem().getRegistryName().equals(ModuleTool.MOB_IMPRISONMENT_TOOL.get().getRegistryName()) && ((MobImprisonmentToolItem)ModuleTool.MOB_IMPRISONMENT_TOOL.get()).containsEntity(stack.getStack())
                    && entity.getType().getRegistryName().toString().equalsIgnoreCase(((MobImprisonmentToolItem)ModuleTool.MOB_IMPRISONMENT_TOOL.get()).getID(stack.getStack()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        boolean isEmpty = true;
        for (GhostSlot stack : this.getFilter()) {
            if (!stack.getStack().isEmpty()) isEmpty = false;
        }
        if (isEmpty) return false;
        for (GhostSlot stack : this.getFilter()) {
            if (itemStack.sameItem(stack.getStack())) return true;
        }
        return false;
    }

    @Override
    public boolean matches(FluidStack fluidStack) {
        boolean isEmpty = true;
        for (GhostSlot stack : this.getFilter()) {
            if (!stack.getStack().isEmpty()) isEmpty = false;
        }
        if (isEmpty) return false;
        for (GhostSlot stack : this.getFilter()) {
            FluidStack original = FluidUtil.getFluidContained(stack.getStack()).orElse(null);
            if (original != null && original.isFluidEqual(fluidStack)) return true;
        }
        return false;
    }

    @Override
    public void setFilter(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.getFilter().length) this.getFilter()[slot].setStack(stack);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        for (int i = 0; i < this.getFilter().length; i++) {
            if (!this.getFilter()[i].getStack().isEmpty())
                compound.put(String.valueOf(i), this.getFilter()[i].getStack().serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (int i = 0; i < this.getFilter().length; i++) {
            if (nbt.contains(String.valueOf(i))) {
                this.getFilter()[i].setStack(ItemStack.of(nbt.getCompound(String.valueOf(i))));
            } else this.getFilter()[i].setStack(ItemStack.EMPTY);
        }
    }
}
