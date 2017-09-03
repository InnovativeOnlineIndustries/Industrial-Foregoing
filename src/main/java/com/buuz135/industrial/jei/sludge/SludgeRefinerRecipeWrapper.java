package com.buuz135.industrial.jei.sludge;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SludgeRefinerRecipeWrapper implements IRecipeWrapper {

    private ItemStackWeightedItem item;
    private int maxWeight;

    public SludgeRefinerRecipeWrapper(ItemStackWeightedItem item, int maxWeight) {
        this.item = item;
        this.maxWeight = maxWeight;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(FluidStack.class, new FluidStack(FluidsRegistry.SLUDGE, 1000));
        ingredients.setOutput(ItemStack.class, item.getStack());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 18 && mouseX <= 58)
            return Arrays.asList("Chance: " + new DecimalFormat("##.##").format((item.itemWeight / (double) maxWeight) * 100) + "%");
        return Arrays.asList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
