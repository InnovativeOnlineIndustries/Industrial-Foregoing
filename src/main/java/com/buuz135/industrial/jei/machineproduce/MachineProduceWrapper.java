package com.buuz135.industrial.jei.machineproduce;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MachineProduceWrapper implements IRecipeWrapper {

    private Block block;
    private ItemStack output;

    public MachineProduceWrapper(Block block, ItemStack output) {
        this.block = block;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(block));
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
