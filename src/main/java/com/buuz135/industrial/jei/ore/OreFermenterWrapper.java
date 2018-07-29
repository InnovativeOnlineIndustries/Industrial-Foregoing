package com.buuz135.industrial.jei.ore;

import com.buuz135.industrial.api.ore.OreFluidEntryFermenter;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

public class OreFermenterWrapper implements IRecipeWrapper {

    private final OreFluidEntryFermenter recipe;

    public OreFermenterWrapper(OreFluidEntryFermenter recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(FluidStack.class, recipe.getInput());
        ingredients.setOutput(FluidStack.class, recipe.getOutput());
    }
}
