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
package com.buuz135.industrial.plugin.jei.generator;

import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class MycelialGeneratorRecipe {

    private final List<List<Ingredient>> inputItems;
    private final List<List<FluidStack>> fluidItems;
    private final int powerTick;
    private final int ticks;

    public MycelialGeneratorRecipe(List<List<Ingredient>> inputItems, List<List<FluidStack>> fluidItems,int ticks, int powerTick) {
        this.inputItems = inputItems;
        this.fluidItems = fluidItems;
        this.powerTick = powerTick;
        this.ticks = ticks;
    }

    public List<List<Ingredient>> getInputItems() {
        return inputItems;
    }

    public List<List<FluidStack>> getFluidItems() {
        return fluidItems;
    }

    public int getPowerTick() {
        return powerTick;
    }

    public int getTicks() {
        return ticks;
    }
}
