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
