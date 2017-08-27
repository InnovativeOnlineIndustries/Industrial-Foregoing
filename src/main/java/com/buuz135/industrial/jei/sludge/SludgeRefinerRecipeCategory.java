package com.buuz135.industrial.jei.sludge;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SludgeRefinerRecipeCategory implements IRecipeCategory<SludgeRefinerRecipeWrapper> {

    private IGuiHelper guiHelper;
    private IDrawable tankOverlay;

    public SludgeRefinerRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        tankOverlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public String getUid() {
        return "sludge_refiner_category";
    }

    @Override
    public String getTitle() {
        return "Sludge Refiner";
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 78, 81, 50);
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SludgeRefinerRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();

        guiFluidStackGroup.init(0, true, 5, 1, 12, 48, 8000, false, tankOverlay);

        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();

        guiItemStackGroup.init(1, false, 59, 17);
        guiFluidStackGroup.set(0, ingredients.getInputs(FluidStack.class).get(0));
        guiItemStackGroup.set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }
}
