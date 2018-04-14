package com.buuz135.industrial.jei.fluiddictionary;

import com.buuz135.industrial.api.recipe.FluidDictionaryEntry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

public class FluidDictionaryWrapper implements IRecipeWrapper {

    private final FluidDictionaryEntry entry;

    public FluidDictionaryWrapper(FluidDictionaryEntry entry) {
        this.entry = entry;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(FluidStack.class, new FluidStack(FluidRegistry.getFluid(entry.getFluidOrigin()), 100));
        ingredients.setOutput(FluidStack.class, new FluidStack(FluidRegistry.getFluid(entry.getFluidResult()), (int) (100 * entry.getRatio())));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Arrays.asList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
