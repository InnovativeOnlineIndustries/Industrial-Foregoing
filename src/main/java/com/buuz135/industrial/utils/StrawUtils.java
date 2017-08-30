package com.buuz135.industrial.utils;

import com.buuz135.industrial.api.fluid.IFluidDrinkHandler;
import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;
import java.util.Map;

public class StrawUtils {
    public static final Map<String,IFluidDrinkHandler> DRINK_HANDLER_MAP = new HashMap<>();

    public static void register(Fluid fluid, IFluidDrinkHandler handler) {
        register(fluid.getName(), handler);
    }
    public static void register(String fluid, IFluidDrinkHandler handler) {
        DRINK_HANDLER_MAP.put(fluid, handler);
    }

    private StrawUtils() {
    }

    public static Map<String, IFluidDrinkHandler> getDrinkHandlers() {
        return DRINK_HANDLER_MAP;
    }
}
