package com.buuz135.industrial.jei.ore;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class OreSieveCategory implements IRecipeCategory<OreSieveWrapper> {

    public final IGuiHelper helper;
    private IDrawable tankOverlay;

    public OreSieveCategory(IGuiHelper helper) {
        this.helper = helper;
        tankOverlay = helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public String getUid() {
        return "ORE_SIEVE";
    }

    @Override
    public String getTitle() {
        return "Sieve";
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 4, 78, 77, 50);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OreSieveWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getFluidStacks().init(0, true, 1, 1, 12, 48, 300, true, tankOverlay);
        recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));

        recipeLayout.getItemStacks().init(1, false, 55, 17);
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }

}
