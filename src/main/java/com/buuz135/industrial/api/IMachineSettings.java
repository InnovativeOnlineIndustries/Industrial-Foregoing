/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2026, Buuz135
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
package com.buuz135.industrial.api;

import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IMachineSettings {

    public static CompoundTag writeInventory(InventoryComponent component) {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < component.getSlots(); i++) {
            var stack = component.getStackInSlot(i);
            if (!stack.isEmpty()) {
                tag.put(i + "", stack.save(new CompoundTag()));
            }
        }
        return tag;
    }

    public static List<ItemStack> readInventory(CompoundTag tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (String allKey : tag.getAllKeys()) {
            stacks.add(ItemStack.of(tag.getCompound(allKey)));
        }
        return stacks;
    }

    void loadSettings(Player player, CompoundTag tag);

    void saveSettings(Player player, CompoundTag tag);
}
