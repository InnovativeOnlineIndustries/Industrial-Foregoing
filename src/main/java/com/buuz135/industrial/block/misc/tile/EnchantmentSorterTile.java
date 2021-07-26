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
package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.misc.EnchantmentSorterConfig;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.items.ItemHandlerHelper;

public class EnchantmentSorterTile extends IndustrialProcessingTile<EnchantmentSorterTile> {

    @Save
    private SidedInventoryComponent<EnchantmentSorterTile> input;
    @Save
    private SidedInventoryComponent<EnchantmentSorterTile> noEnchanted;
    @Save
    private SidedInventoryComponent<EnchantmentSorterTile> enchantedItems;

    public EnchantmentSorterTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleMisc.ENCHANTMENT_SORTER, 57, 40, blockPos, blockState);
        this.addInventory(input = (SidedInventoryComponent<EnchantmentSorterTile>) new SidedInventoryComponent<EnchantmentSorterTile>("input", 35, 40, 1, 0).
                setColor(DyeColor.BLUE).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(noEnchanted = (SidedInventoryComponent<EnchantmentSorterTile>) new SidedInventoryComponent<EnchantmentSorterTile>("noEnchanted", 90, 20, 4, 1).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(enchantedItems = (SidedInventoryComponent<EnchantmentSorterTile>) new SidedInventoryComponent<EnchantmentSorterTile>("enchanted", 90, 60, 4, 2).
                setColor(DyeColor.MAGENTA).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    @Override
    public EnchantmentSorterTile getSelf() {
        return this;
    }

    @Override
    public boolean canIncrease() {
        return !input.getStackInSlot(0).isEmpty() && ((isEnchanted(input.getStackInSlot(0)) && ItemHandlerHelper.insertItem(enchantedItems, input.getStackInSlot(0).copy(), true).isEmpty()) || (!isEnchanted(input.getStackInSlot(0)) && ItemHandlerHelper.insertItem(noEnchanted, input.getStackInSlot(0).copy(), true).isEmpty()));
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack stack = input.getStackInSlot(0).copy();
            ItemHandlerHelper.insertItem(isEnchanted(stack) ? enchantedItems : noEnchanted, stack, false);
            input.setStackInSlot(0, ItemStack.EMPTY);
        };
    }

    @Override
    protected EnergyStorageComponent<EnchantmentSorterTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(EnchantmentSorterConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return EnchantmentSorterConfig.powerPerTick;
    }

    @Override
    public int getMaxProgress() {
        return EnchantmentSorterConfig.maxProgress;
    }

    private boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem;
    }
}
