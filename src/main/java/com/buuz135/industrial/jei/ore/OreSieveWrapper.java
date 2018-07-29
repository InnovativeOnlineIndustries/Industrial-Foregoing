package com.buuz135.industrial.jei.ore;

import com.buuz135.industrial.api.ore.OreFluidEntrySieve;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class OreSieveWrapper implements IRecipeWrapper {

    private final OreFluidEntrySieve recipe;

    public OreSieveWrapper(OreFluidEntrySieve recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(FluidStack.class, recipe.getInput());
        ingredients.setOutput(ItemStack.class, recipe.getOutput());
    }
}
