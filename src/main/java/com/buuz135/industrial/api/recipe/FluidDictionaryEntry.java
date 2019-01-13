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
package com.buuz135.industrial.api.recipe;

import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FluidDictionaryEntry {

    public static final List<FluidDictionaryEntry> FLUID_DICTIONARY_RECIPES = new ArrayList<>();

    private String fluidOrigin;
    private String fluidResult;
    private double ratio;

    /**
     * Default constructor
     *
     * @param fluidOrigin The fluid name of the input fluid
     * @param fluidResult The fluid name of the output fluid
     * @param ratio       The transformation ratio (100mb of the fluidOrigin * ratio = mb of the fluidResult)
     */
    public FluidDictionaryEntry(String fluidOrigin, String fluidResult, double ratio) {
        this.fluidOrigin = fluidOrigin;
        this.fluidResult = fluidResult;
        this.ratio = ratio;
    }

    public boolean doesStackMatch(FluidStack fluidStack) {
        return fluidStack.getFluid().getName().equals(fluidOrigin);
    }

    public String getFluidOrigin() {
        return fluidOrigin;
    }

    public String getFluidResult() {
        return fluidResult;
    }

    public double getRatio() {
        return ratio;
    }
}
