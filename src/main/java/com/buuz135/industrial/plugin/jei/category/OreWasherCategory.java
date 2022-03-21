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

import java.awt.Color;
import java.util.stream.Collectors;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class OreWasherCategory implements IRecipeCategory<OreFluidEntryRaw> {

    public static ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "ore_washer");

    private final IGuiHelper helper;
    private IDrawable tankOverlay;

    public OreWasherCategory(IGuiHelper helper) {
        this.helper = helper;
        tankOverlay = helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends OreFluidEntryRaw> getRecipeClass() {
        return OreFluidEntryRaw.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent(ModuleResourceProduction.WASHING_FACTORY.getLeft().get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 142, 29, 112, 50);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(OreFluidEntryRaw oreWasherWrapper, IIngredients iIngredients) {
        iIngredients.setInputs(VanillaTypes.ITEM, oreWasherWrapper.getOre().getValues().stream().map(ItemStack::new).collect(Collectors.toList()));
        iIngredients.setInput(VanillaTypes.FLUID, oreWasherWrapper.getInput());
        iIngredients.setOutput(VanillaTypes.FLUID, oreWasherWrapper.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OreFluidEntryRaw recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, true, 0, 15);

        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
        guiFluidStackGroup.init(1, true, 47, 1, 12, 48, 200, false, tankOverlay);
        guiFluidStackGroup.init(2, false, 99, 1, 12, 48, 200, false, tankOverlay);

        guiItemStackGroup.set(0, recipeWrapper.getOre().getValues().stream().map(ItemStack::new).collect(Collectors.toList()));
        guiFluidStackGroup.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        guiFluidStackGroup.set(2, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }

    @Override
    public void draw(OreFluidEntryRaw recipe, PoseStack stack, double mouseX, double mouseY) {
        SlotsScreenAddon.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 1,16, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.BLUE.getFireworkColor()), integer -> true);
    }
}
