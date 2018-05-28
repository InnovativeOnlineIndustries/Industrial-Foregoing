package com.buuz135.industrial.utils;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;

public class FluidUtils {

    public static HashMap<Fluid, Integer> colorCache = new HashMap<>();

    public static int getFluidColor(FluidStack stack) {
        int color = -1;
        if (stack != null && stack.getFluid() != null) {
            color = colorCache.getOrDefault(stack.getFluid().getStill(stack), stack.getFluid().getColor(stack) != 0xffffffff ? stack.getFluid().getColor(stack) : ColorUtils.getColorFrom(stack.getFluid().getStill(stack)));
            if (!colorCache.containsKey(stack.getFluid())) colorCache.put(stack.getFluid(), color);
        }
        return color;
    }

    public static int getFluidColor(Fluid fluid) {
        int color = -1;
        if (fluid != null) {
            color = colorCache.getOrDefault(fluid.getStill(), fluid.getColor() != 0xffffffff ? fluid.getColor() : ColorUtils.getColorFrom(fluid.getStill()));
            if (!colorCache.containsKey(fluid)) colorCache.put(fluid, color);
        }
        return color;
    }
}
