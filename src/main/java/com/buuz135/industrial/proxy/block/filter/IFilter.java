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

import com.buuz135.industrial.gui.component.custom.ICanSendNetworkMessage;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.gui.transporter.GuiTransporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;


public interface IFilter<T extends Entity> {

    boolean acceptsInput(ItemStack stack);

    boolean matches(T entity);

    boolean matches(ItemStack itemStack);

    boolean matches(FluidStack fluidStack);

    void setFilter(int slot, ItemStack stack);

    GhostSlot[] getFilter();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);

    public static class GhostSlot {

        public static final int MAX = 1024;
        public static final int MIN = 1;

        private final int x;
        private final int y;
        private int id;
        private ItemStack stack;
        private int amount;
        private int maxAmount;

        public GhostSlot(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.stack = ItemStack.EMPTY;
            this.amount = 1;
            this.maxAmount = MAX;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        public void increaseAmount(int amount) {
            this.amount = Math.max(0, Math.min(this.maxAmount, this.amount + amount));
        }

        public void decreaseAmount(int amount) {
            this.amount = Math.max(MIN, this.amount - amount);
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public Rect2i getArea() {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof GuiConveyor) {
                GuiConveyor gui = (GuiConveyor) screen;
                return new Rect2i(x + gui.getX(), y + gui.getY(), 18, 18);
            }
            if (screen instanceof GuiTransporter) {
                GuiTransporter gui = (GuiTransporter) screen;
                return new Rect2i(x + gui.getX(), y + gui.getY(), 18, 18);
            }
            return new Rect2i(0, 0, 0, 0);
        }

        public void accept(ItemStack ingredient) {
            if (Minecraft.getInstance().screen instanceof ICanSendNetworkMessage) {
                ((ICanSendNetworkMessage) Minecraft.getInstance().screen).sendMessage(id, ingredient.serializeNBT());
            }
        }
    }

}
