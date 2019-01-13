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
package com.buuz135.industrial.proxy.block.filter;

import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import java.awt.*;


public interface IFilter<T extends Entity> {

    boolean acceptsInput(ItemStack stack);

    boolean matches(T entity);

    boolean matches(ItemStack itemStack);

    boolean matches(FluidStack fluidStack);

    void setFilter(int slot, ItemStack stack);

    GhostSlot[] getFilter();

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound nbt);

    @Optional.Interface(iface = "mezz.jei.api.gui.IGhostIngredientHandler$Target", modid = "jei", striprefs = true)
    public static class GhostSlot implements IGhostIngredientHandler.Target<ItemStack> {

        private final int x;
        private final int y;
        private int id;
        private ItemStack stack;

        public GhostSlot(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.stack = ItemStack.EMPTY;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Rectangle getArea() {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiConveyor) {
                GuiConveyor gui = (GuiConveyor) Minecraft.getMinecraft().currentScreen;
                return new Rectangle(x + gui.getX(), y + gui.getY(), 18, 18);
            }
            return new Rectangle();
        }

        @Override
        public void accept(ItemStack ingredient) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiConveyor) {
                ((GuiConveyor) Minecraft.getMinecraft().currentScreen).sendMessage(id, ingredient.serializeNBT());
            }
        }
    }

}
