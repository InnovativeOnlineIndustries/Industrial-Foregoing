package com.buuz135.industrial.jei.stonework;

import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.List;

public class StoneWorkWrapper implements IRecipeWrapper {

    private final List<MaterialStoneWorkFactoryTile.Mode> modes;
    private final ItemStack output;

    public StoneWorkWrapper(List<MaterialStoneWorkFactoryTile.Mode> modes, ItemStack output) {
        this.modes = modes;
        this.output = output;
        while (this.modes.size() < 4) {
            this.modes.add(MaterialStoneWorkFactoryTile.Mode.NONE);
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(Blocks.COBBLESTONE));
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        for (int i = 0; i < modes.size(); i++) {
            ItemStackUtils.renderItemIntoGUI(modes.get(i).getItemStack(), 29 + i * 24, 5, 7);
        }
    }

    public List<MaterialStoneWorkFactoryTile.Mode> getModes() {
        return modes;
    }

    public ItemStack getOutput() {
        return output;
    }
}
