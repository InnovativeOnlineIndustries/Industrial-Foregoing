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
package com.buuz135.industrial.api.recipe.ore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OreFluidEntryRaw {

    public static List<OreFluidEntryRaw> ORE_RAW_ENTRIES = new ArrayList<>();

    private ITag<Item> ore;
    private FluidStack input;
    private FluidStack output;
    private Collection<Item> cachedOres;

    public OreFluidEntryRaw(ITag<Item> ore, FluidStack input, FluidStack output) {
        this.ore = ore;
        this.input = input;
        this.output = output;
        this.cachedOres = ore.getAllElements();
    }

    public ITag<Item> getOre() {
        return ore;
    }

    public FluidStack getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }

    public boolean matches(ItemStack item, FluidStack fluid) {
        for (Item cachedOre : cachedOres) {
            if (cachedOre.equals(item.getItem()) && input.isFluidEqual(fluid)) return true;
        }
        return false;
    }
}
