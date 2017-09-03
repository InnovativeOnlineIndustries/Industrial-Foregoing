package com.buuz135.industrial.api.fluid;

import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;
import java.util.Map;

public class StrawHelper {

    public static final Map<String, IFluidDrinkHandler> DRINK_HANDLER_MAP = new HashMap<>();

    private StrawHelper() {
    }

    public static void register(Fluid fluid, IFluidDrinkHandler handler) {
        register(fluid.getName(), handler);
    }

    public static void register(String fluid, IFluidDrinkHandler handler) {
        DRINK_HANDLER_MAP.put(fluid, handler);
    }

    public static Map<String, IFluidDrinkHandler> getDrinkHandlers() {
        return DRINK_HANDLER_MAP;
    }
}
