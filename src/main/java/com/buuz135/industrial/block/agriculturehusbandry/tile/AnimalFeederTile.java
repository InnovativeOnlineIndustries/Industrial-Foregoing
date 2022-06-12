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
package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.AnimalFeederConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class AnimalFeederTile extends IndustrialAreaWorkingTile<AnimalFeederTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedInventoryComponent<AnimalFeederTile> input;

    public AnimalFeederTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.ANIMAL_FEEDER, RangeManager.RangeType.BEHIND, true, AnimalFeederConfig.powerPerOperation, blockPos, blockState);
        addInventory(input = (SidedInventoryComponent<AnimalFeederTile>) new SidedInventoryComponent<AnimalFeederTile>("food", 53, 22, 6 * 3, 0)
                .setColor(DyeColor.BLUE)
                .setOutputFilter((stack, integer) -> false)
                .setRange(6, 3)
                .setComponentHarness(this)
        );
        this.maxProgress = AnimalFeederConfig.maxProgress;
        this.powerPerOperation = AnimalFeederConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            List<Animal> mobs = this.level.getEntitiesOfClass(Animal.class, getWorkingArea().bounds());
            if (mobs.size() == 0 || mobs.size() > AnimalFeederConfig.maxAnimalInTheArea) return new WorkAction(1, 0);
            mobs.removeIf(animalEntity -> animalEntity.getAge() != 0 || !animalEntity.canFallInLove() || getFeedingItem(animalEntity).isEmpty());
            for (Animal firstParent : mobs) {
                for (Animal secondParent : mobs) {
                    if (firstParent.equals(secondParent) || !firstParent.getClass().equals(secondParent.getClass()))
                        continue;
                    ItemStack stack = getFeedingItem(firstParent);
                    ItemStack original = stack.copy();
                    stack.shrink(1);
                    stack = getFeedingItem(secondParent);
                    if (stack.isEmpty()) {
                        original.setCount(1);
                        ItemHandlerHelper.insertItem(input, original, false);
                        continue;
                    }
                    stack.shrink(1);
                    firstParent.setInLove(null);
                    secondParent.setInLove(null);
                    return new WorkAction(0.5f, powerPerOperation);
                }
            }
        }
        return new WorkAction(1, 0);
    }

    private ItemStack getFeedingItem(Animal entity) {
        for (int i = 0; i < input.getSlots(); i++) {
            if (entity.isFood(input.getStackInSlot(i))) return input.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected EnergyStorageComponent<AnimalFeederTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(AnimalFeederConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }


    @Nonnull
    @Override
    public AnimalFeederTile getSelf() {
        return this;
    }
}
