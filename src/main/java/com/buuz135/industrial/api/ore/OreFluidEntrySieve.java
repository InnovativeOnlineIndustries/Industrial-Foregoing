package com.buuz135.industrial.api.ore;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class OreFluidEntrySieve {

    public static List<OreFluidEntrySieve> ORE_FLUID_SIEVE = new ArrayList<>();

    private final FluidStack input;
    private final ItemStack output;

    public OreFluidEntrySieve(FluidStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public FluidStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}
