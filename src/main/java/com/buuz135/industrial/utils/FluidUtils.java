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
package com.buuz135.industrial.utils;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;

public class FluidUtils {

    public static HashMap<Fluid, Integer> colorCache = new HashMap<>();

    public static int getFluidColor(FluidStack stack) {
        int color = -1;
        if (stack != null && stack.getFluid() != null) {
            color = colorCache.getOrDefault(stack.getFluid().getAttributes().getStillTexture(stack), stack.getFluid().getAttributes().getColor(stack) != 0xffffffff ? stack.getFluid().getAttributes().getColor(stack) : ColorUtils.getColorFrom(stack.getFluid().getAttributes().getStillTexture(stack)));
            if (!colorCache.containsKey(stack.getFluid())) colorCache.put(stack.getFluid(), color);
        }
        return color;
    }

    public static int getFluidColor(Fluid fluid) {
        int color = -1;
        if (fluid != null) {
            color = colorCache.getOrDefault(fluid, fluid.getAttributes().getColor() != 0xffffffff ? fluid.getAttributes().getColor() : ColorUtils.getColorFrom(fluid.getAttributes().getStillTexture()));
            if (!colorCache.containsKey(fluid)) colorCache.put(fluid, color);
        }
        return color;
    }
}
