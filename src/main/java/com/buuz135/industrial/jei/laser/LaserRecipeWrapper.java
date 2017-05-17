package com.buuz135.industrial.jei.laser;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class LaserRecipeWrapper implements IRecipeWrapper {

    private ItemStackWeightedItem item;
    private int maxWeight;
    private int lens;

    public LaserRecipeWrapper(ItemStackWeightedItem item, int maxWeight, int lens) {
        this.item = item;
        this.maxWeight = maxWeight;
        this.lens = lens;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(ItemRegistry.laserLensItem, 1, lens));
        ingredients.setOutput(ItemStack.class, item.getStack());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Arrays.asList("Chance: " + new DecimalFormat("##.##").format((item.itemWeight / (double) maxWeight) * 100) + "%");
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
