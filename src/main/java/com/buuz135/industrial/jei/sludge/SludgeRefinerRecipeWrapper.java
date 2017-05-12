package com.buuz135.industrial.jei.sludge;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.UniversalBucket;

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
        ingredients.setInput(ItemStack.class, UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidsRegistry.SLUDGE));
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
