package com.buuz135.industrial.api.ore;

import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class OreFluidEntryFermenter {

    public static List<OreFluidEntryFermenter> ORE_FLUID_FERMENTER = new ArrayList<>();

    private final FluidStack input;
    private final FluidStack output;

    public OreFluidEntryFermenter(FluidStack input, FluidStack output) {
        this.input = input;
        this.output = output;
    }

    public FluidStack getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }
}
