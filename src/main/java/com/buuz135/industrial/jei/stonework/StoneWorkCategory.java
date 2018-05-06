package com.buuz135.industrial.jei.stonework;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StoneWorkCategory implements IRecipeCategory<StoneWorkWrapper> {

    private final IGuiHelper helper;

    public StoneWorkCategory(IGuiHelper helper) {
        this.helper = helper;
    }


    @Override
    public String getUid() {
        return "stone_work_factory";
    }

    @Override
    public String getTitle() {
        return "Material StoneWork Factory";
    }

    @Override
    public String getModName() {
        return Reference.MOD_ID;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 94, 0, 160, 26);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, StoneWorkWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 0, 4);
        recipeLayout.getItemStacks().set(0, new ItemStack(Blocks.COBBLESTONE));

        recipeLayout.getItemStacks().init(1, false, 138, 4);
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }

}
