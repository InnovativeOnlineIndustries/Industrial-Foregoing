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
package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class OreProcessorTile extends CustomElectricMachine {

    private ItemStackHandler input;
    private IFluidTank tank;
    private ItemStackHandler output;

    public OreProcessorTile() {
        super(OreProcessorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                OreProcessorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(input, EnumDyeColor.BLUE, "Ores input", 18 * 2 + 12, 25, 1, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if (ItemStackUtils.isOre(stack)) {
                    if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
                        Block block = Block.getBlockFromItem(stack.getItem());
                        NonNullList<ItemStack> stacks = NonNullList.create();
                        block.getDrops(stacks, OreProcessorTile.this.world, OreProcessorTile.this.pos, block.getStateFromMeta(stack.getMetadata()), 0);
                        if (stacks.size() > 0 && !stacks.get(0).isItemEqual(stack)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(input, "input");
        tank = this.addFluidTank(FluidsRegistry.ESSENCE, 8000, EnumDyeColor.GREEN, "Essence tank", new BoundingRectangle(18 * 4 - 2, 25, 18, 54));
        output = new ItemStackHandler(3 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                OreProcessorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(output, EnumDyeColor.ORANGE, "Processed ores output", 18 * 6 + 2, 25, 3, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(output, "outout");
    }

    private ItemStack getFirstStack() {
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!input.getStackInSlot(i).isEmpty()) return input.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        ItemStack stack = getFirstStack();
        if (stack.isEmpty()) return 0;
        Block block = Block.getBlockFromItem(stack.getItem());
        int fortune = getFortuneLevel();
        NonNullList<ItemStack> stacks = NonNullList.create();
        block.getDrops(stacks, OreProcessorTile.this.world, OreProcessorTile.this.pos, block.getStateFromMeta(stack.getMetadata()), fortune);
        boolean canInsert = true;
        for (ItemStack temp : stacks) {
            if (!ItemHandlerHelper.insertItem(output, temp, true).isEmpty()) {
                canInsert = false;
                break;
            }
        }
        if (canInsert) {
            for (ItemStack temp : stacks) {
                ItemHandlerHelper.insertItem(output, temp, false);
            }
            tank.drain(fortune * BlockRegistry.oreProcessorBlock.getEssenceFortune(), true);
            stack.setCount(stack.getCount() - 1);
            return 1;
        }
        return 0;
    }

    private int getFortuneLevel() {
        for (int i = 3; i > 0; i--) {
            if (i * BlockRegistry.oreProcessorBlock.getEssenceFortune() <= tank.getFluidAmount()) return i;
        }
        return 0;
    }
}
