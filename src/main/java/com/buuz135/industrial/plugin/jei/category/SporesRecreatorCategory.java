/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.plugin.jei.category;

import com.buuz135.industrial.config.machine.core.LatexProcessingUnitConfig;
import com.buuz135.industrial.config.machine.resourceproduction.SporeRecreatorConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.Arrays;

public class SporesRecreatorCategory implements IRecipeCategory<SporesRecreatorCategory.Recipe> {
    public record Recipe(Ingredient ingredient) {}
    public static final RecipeType<Recipe> RECIPE_TYPE = RecipeType.create(Reference.MOD_ID, "dummy/spores_recreator", Recipe.class);

    private final IGuiHelper helper;
    private final IDrawable bigTank;

    public SporesRecreatorCategory(IGuiHelper helper) {
        this.helper = helper;
        this.bigTank = helper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3, 12, 50);
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModuleResourceProduction.SPORES_RECREATOR.getBlock().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(120, 60);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 24 + 3, 3 + 3).setFluidRenderer(SporeRecreatorConfig.maxWaterTankSize, false, 12, 50).setOverlay(bigTank, 0, 0).addIngredient(NeoForgeTypes.FLUID_STACK, new FluidStack(Fluids.WATER, 100));
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 23).addIngredients(recipe.ingredient);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 5).addIngredients(Ingredient.of(Arrays.stream(recipe.ingredient.getItems()).map(i -> i.copyWithCount(2))));
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND), 0, 3);
        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 24, 3);
        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL), 72, 22);

        SlotsScreenAddon.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 48, 23, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.LIGHT_BLUE.getFireworkColor()), integer -> true, 1);
        SlotsScreenAddon.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 100, 5, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);

        int consumed = SporeRecreatorConfig.powerPerTick * 100;
        EnergyBarScreenAddon.drawForeground(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 3, 0, 0, consumed, Math.max(SporeRecreatorConfig.maxStoredPower, consumed));
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, Recipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Rectangle rec = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND).getArea();
        if (new Rectangle(0, 3, rec.width, rec.height).contains(mouseX, mouseY)) {
            int consumed = SporeRecreatorConfig.powerPerTick * 100;
            tooltip.addAll(EnergyBarScreenAddon.getTooltip(consumed, Math.max(LatexProcessingUnitConfig.maxStoredPower, consumed)));
        }
    }
}
