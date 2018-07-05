package com.buuz135.industrial.jei.extractor;

import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtractorRecipeCategory implements IRecipeCategory<ExtractorRecipeWrapper> {

    public static String UID = "EXTRACTOR_RECIPE";

    private IGuiHelper guiHelper;
    private IDrawable tankOverlay;

    public ExtractorRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.tankOverlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return "Tree Fluid Extractor";
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 27, 82, 50);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ExtractorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, true, 0, 16);

        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
        guiFluidStackGroup.init(1, false, 57, 1, 12, 48, Math.max(50, ingredients.getOutputs(FluidStack.class).get(0).get(0).amount), false, tankOverlay);

        guiItemStackGroup.set(0, ingredients.getInputs(ItemStack.class).get(0));
        guiFluidStackGroup.set(1, ingredients.getOutputs(FluidStack.class).get(0));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 18 && mouseX <= 58) return Arrays.asList("Production rate");
        return new ArrayList<>();
    }
}
