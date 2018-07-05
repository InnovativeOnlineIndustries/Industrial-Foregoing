package com.buuz135.industrial.jei.extractor;

import com.buuz135.industrial.api.extractor.ExtractorEntry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ExtractorRecipeWrapper implements IRecipeWrapper {

    public final ExtractorEntry extractorEntry;

    public ExtractorRecipeWrapper(ExtractorEntry extractorEntry) {
        this.extractorEntry = extractorEntry;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, extractorEntry.getItemStack());
        ingredients.setOutput(FluidStack.class, extractorEntry.getFluidStack());
    }
}
