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

package com.buuz135.industrial.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.ConcurrentHashMap;

public class FluidUtils {
    // TODO
    // should item colors from ItemStackUtils also be cached?
    // invalidate cache on resource reload
    public static ConcurrentHashMap<ResourceLocation, Integer> colorCache = new ConcurrentHashMap<>();

    public static int getFluidColor(FluidStack stack) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation location = renderProperties.getStillTexture(stack);
        int tint = renderProperties.getTintColor(stack);
        int textureColor = colorCache.computeIfAbsent(location, ColorUtils::getColorFrom);
        return FastColor.ARGB32.multiply(textureColor, tint);
    }

    public static int getFluidColor(Fluid fluid) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation location = renderProperties.getStillTexture();
        int tint = renderProperties.getTintColor();
        int textureColor = colorCache.computeIfAbsent(location, ColorUtils::getColorFrom);
        return FastColor.ARGB32.multiply(textureColor, tint);
    }
}