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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.items.ItemStackHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class AnimalGrowthIncreaserTile extends WorkingAreaElectricMachine {

    private ItemStackHandler items;

    public AnimalGrowthIncreaserTile() {
        super(AnimalGrowthIncreaserTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        items = new ItemStackHandler(6 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                AnimalGrowthIncreaserTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(items, EnumDyeColor.GREEN, "Food items", 18 * 3, 25, 6, 3));
        this.addInventoryToStorage(items, "items");
    }

    @Override
    public float work() {
        AtomicBoolean hasWorked = new AtomicBoolean(false);
        world.getEntitiesWithinAABB(EntityAnimal.class, getWorkingArea()).stream().filter(EntityAgeable::isChild).filter(entityAnimal -> !BlockRegistry.animalGrowthIncreaserBlock.entityBlacklist.contains(EntityList.getKey(entityAnimal).toString())).forEach(entityAnimal -> {
            for (int i = 0; i < items.getSlots(); ++i) {
                if (entityAnimal.isBreedingItem(items.getStackInSlot(i))) {
                    entityAnimal.ageUp(30, true);
                    items.getStackInSlot(i).shrink(1);
                    hasWorked.set(true);
                    break;
                }
            }
        });
        return hasWorked.get() ? 1 : 0;
    }

}
