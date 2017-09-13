package com.buuz135.industrial.jei.petrifiedgen;

import com.buuz135.industrial.tile.generator.PetrifiedFuelGeneratorTile;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

public class PetrifiedBurnTimeWrapper implements IRecipeWrapper {

    private ItemStack stack;
    private int burnTime;

    public PetrifiedBurnTimeWrapper(ItemStack stack, int burnTime) {
        this.stack = stack;
        this.burnTime = burnTime;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, stack);
    }


    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString("Power: " + PetrifiedFuelGeneratorTile.getEnergyProduced(this.burnTime) + " T/tick", 24, 7, Color.gray.getRGB());
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getBurnTime() {
        return burnTime;
    }
}
