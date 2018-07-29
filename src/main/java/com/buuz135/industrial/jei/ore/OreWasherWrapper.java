package com.buuz135.industrial.jei.ore;

import com.buuz135.industrial.api.ore.OreFluidEntryRaw;
import com.buuz135.industrial.utils.ItemStackUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class OreWasherWrapper implements IRecipeWrapper {

    private final OreFluidEntryRaw entryRaw;

    public OreWasherWrapper(OreFluidEntryRaw entryRaw) {
        this.entryRaw = entryRaw;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, ItemStackUtils.getOreItems(entryRaw.getOre()));
        ingredients.setInput(FluidStack.class, entryRaw.getInput());
        ingredients.setOutput(FluidStack.class, entryRaw.getOutput());
    }
}
