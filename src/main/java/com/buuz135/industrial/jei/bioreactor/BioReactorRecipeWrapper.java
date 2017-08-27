package com.buuz135.industrial.jei.bioreactor;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BioReactorRecipeWrapper implements IRecipeWrapper {

    private ItemStack stack;

    public BioReactorRecipeWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, stack);
        ingredients.setOutput(FluidStack.class, new FluidStack(FluidsRegistry.BIOFUEL, BlockRegistry.bioReactorBlock.getBaseAmount()));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 18 && mouseX <= 58)
            return Arrays.asList("Efficiency", " Min: " + BlockRegistry.bioReactorBlock.getBaseAmount() + "mb/item", " Max: " + BlockRegistry.bioReactorBlock.getBaseAmount() * 2 + "mb/item");
        return new ArrayList<>();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
