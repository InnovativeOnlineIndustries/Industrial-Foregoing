package com.buuz135.industrial.plugin.jei.generator;

import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class MycelialGeneratorRecipe {

    private final List<List<Ingredient>> inputItems;
    private final List<List<FluidStack>> fluidItems;
    private final int powerTick;
    private final int ticks;

    public MycelialGeneratorRecipe(List<List<Ingredient>> inputItems, List<List<FluidStack>> fluidItems,int ticks, int powerTick) {
        this.inputItems = inputItems;
        this.fluidItems = fluidItems;
        this.powerTick = powerTick;
        this.ticks = ticks;
    }

    public List<List<Ingredient>> getInputItems() {
        return inputItems;
    }

    public List<List<FluidStack>> getFluidItems() {
        return fluidItems;
    }

    public int getPowerTick() {
        return powerTick;
    }

    public int getTicks() {
        return ticks;
    }
}
