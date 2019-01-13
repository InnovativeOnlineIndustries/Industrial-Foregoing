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
package com.buuz135.industrial.api.extractor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ExtractorEntry {

    public static List<ExtractorEntry> EXTRACTOR_ENTRIES = new ArrayList<>();

    private final ItemStack itemStack;
    private final FluidStack fluidStack;
    private final float breakChance;

    public ExtractorEntry(ItemStack itemStack, FluidStack fluidStack) {
        this(itemStack, fluidStack, 0.005f);
    }

    public ExtractorEntry(ItemStack itemStack, FluidStack fluidStack, float breakChance) {
        this.itemStack = itemStack;
        this.fluidStack = fluidStack;
        this.breakChance = breakChance;
    }

    public static ExtractorEntry getExtractorEntry(World world, BlockPos pos) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(world.getBlockState(pos).getBlock()), 1, world.getBlockState(pos).getBlock().damageDropped(world.getBlockState(pos)));
        if (!stack.isEmpty()) {
            for (ExtractorEntry extractorEntry : EXTRACTOR_ENTRIES) {
                if (extractorEntry.isEqual(stack)) return extractorEntry;
            }
        }
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }

    public boolean isEqual(ItemStack stack) {
        return stack.isItemEqual(this.getItemStack());
    }

    public float getBreakChance() {
        return breakChance;
    }
}
