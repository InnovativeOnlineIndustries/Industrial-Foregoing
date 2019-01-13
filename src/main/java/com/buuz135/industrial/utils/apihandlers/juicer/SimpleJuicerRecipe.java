package com.buuz135.industrial.utils.apihandlers.juicer;

import com.buuz135.industrial.api.juicer.JuicerRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class SimpleJuicerRecipe extends JuicerRecipe {
    private Ingredient ingredient;
    private FluidStack output;

    public SimpleJuicerRecipe(String name, Ingredient ingredient, FluidStack output) {
        setRegistryName(name);
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public FluidStack getOutput() {
        return output.copy();
    }
}