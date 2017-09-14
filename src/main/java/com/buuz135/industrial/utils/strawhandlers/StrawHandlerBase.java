package com.buuz135.industrial.utils.strawhandlers;

import com.buuz135.industrial.api.straw.StrawHandler;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class StrawHandlerBase extends StrawHandler {
    private String fluidName;

    public StrawHandlerBase(String fluidName) {
        this.fluidName = fluidName;
    }

    @Override
    public boolean validFluid(FluidStack stack) {
        return stack.getFluid().getName().equals(fluidName);
    }
}
