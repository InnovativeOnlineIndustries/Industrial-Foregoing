package com.buuz135.industrial.jei.manual;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;

public class ManualCategory implements IRecipeCategory<ManualWrapper> {

    private final IDrawable drawable;

    public ManualCategory(IGuiHelper helper) {
        this.drawable = helper.createBlankDrawable(160, 125);
    }

    @Override
    public String getUid() {
        return "if_manual_category";
    }

    @Override
    public String getTitle() {
        return "Industrial Foregoing's Manual";
    }

    @Override
    public String getModName() {
        return Reference.MOD_ID;
    }

    @Override
    public IDrawable getBackground() {
        return drawable;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ManualWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 70, 0);
        recipeLayout.getItemStacks().set(0, recipeWrapper.getEntry().getDisplay());
    }
}
