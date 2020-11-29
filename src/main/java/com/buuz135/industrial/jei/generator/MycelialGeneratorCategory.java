package com.buuz135.industrial.jei.generator;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class MycelialGeneratorCategory implements IRecipeCategory<MycelialGeneratorRecipe> {

    private IGuiHelper guiHelper;
    private IMycelialGeneratorType type;
    private final IDrawable smallTank;

    public MycelialGeneratorCategory(IMycelialGeneratorType type, IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.type = type;
        this.smallTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 235 + 3, 1 + 3, 12, 13);
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Reference.MOD_ID, "mycelial_" + type.getName());
    }

    @Override
    public Class<? extends MycelialGeneratorRecipe> getRecipeClass() {
        return MycelialGeneratorRecipe.class;
    }

    @Override
    public String getTitle() {
        return "industrialforegoing.jei.category." + type.getName();
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createBlankDrawable(20*type.getInputs().length + 110,  Minecraft.getInstance().fontRenderer.FONT_HEIGHT * 3);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(MycelialGeneratorRecipe recipe, IIngredients iIngredients) {
        for (int i = 0; i < type.getInputs().length; i++) {
            if (type.getInputs()[i] == IMycelialGeneratorType.Input.SLOT){
                iIngredients.setInputLists(VanillaTypes.ITEM, recipe.getInputItems());
            } else if (type.getInputs()[i] == IMycelialGeneratorType.Input.TANK){
                iIngredients.setInputLists(VanillaTypes.FLUID, recipe.getFluidItems());
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, MycelialGeneratorRecipe recipe, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStackGroup = iRecipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStackGroup = iRecipeLayout.getFluidStacks();
        for (int i = 0; i < type.getInputs().length; i++) {
            if (type.getInputs()[i] == IMycelialGeneratorType.Input.SLOT){
                guiItemStackGroup.init(i, true, 20*i , Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2);
                guiItemStackGroup.set(i, recipe.getInputItems().get(i));
            } else if (type.getInputs()[i] == IMycelialGeneratorType.Input.TANK){
                guiFluidStackGroup.init(i, true, 20*i +3, 3 + Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2, 12, 13, 1000, false,smallTank);
                guiFluidStackGroup.set(i, recipe.getFluidItems().get(i));
            }
        }
    }

    @Override
    public void draw(MycelialGeneratorRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        for (int i = 0; i < type.getInputs().length; i++) {
            if (type.getInputs()[i] == IMycelialGeneratorType.Input.SLOT){
                int finalI = i;
                SlotsScreenAddon.drawAsset(matrixStack, Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER, 20*i , Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2, 0, 0, 1, integer -> Pair.of(1,1), integer -> ItemStack.EMPTY, true, integer -> new Color(type.getInputColors()[finalI].getFireworkColor()), integer -> true);
            } else if (type.getInputs()[i] == IMycelialGeneratorType.Input.TANK){
                AssetUtil.drawAsset(matrixStack, Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 20*i , Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2);
            }
        }
        int x = 20*type.getInputs().length + 3;
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "Time: " + TextFormatting.DARK_AQUA+ new DecimalFormat( ).format(recipe.getTicks() / 20D) +TextFormatting.DARK_GRAY + " s", x, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * 0, 0xFFFFFFFF);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "Production: " + TextFormatting.DARK_AQUA+ recipe.getPowerTick() +TextFormatting.DARK_GRAY + " FE/t", x, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * 1, 0xFFFFFFFF);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "Total: " + TextFormatting.DARK_AQUA+ new DecimalFormat( ).format(recipe.getTicks() * recipe.getPowerTick())+ TextFormatting.DARK_GRAY + " FE", x, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * 2, 0xFFFFFFFF);


    }

    @Override
    public List<ITextComponent> getTooltipStrings(MycelialGeneratorRecipe recipe, double mouseX, double mouseY) {
        return Collections.emptyList();
    }

}
