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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FluidExtractorCategory implements IRecipeCategory<FluidExtractorRecipe> {

    private IGuiHelper guiHelper;
    private IDrawable tankOverlay;

    public FluidExtractorCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.tankOverlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public ResourceLocation getUid() {
        return IndustrialRecipeTypes.FLUID_EXTRACTOR.getUid();
    }

    @Override
    public Class<? extends FluidExtractorRecipe> getRecipeClass() {
        return IndustrialRecipeTypes.FLUID_EXTRACTOR.getRecipeClass();
    }

    @Override
    public RecipeType<FluidExtractorRecipe> getRecipeType() {
        return IndustrialRecipeTypes.FLUID_EXTRACTOR;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent(ModuleCore.FLUID_EXTRACTOR.getLeft().get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 27, 76, 50).addPadding(0, 0, 0, 74).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidExtractorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 17).addIngredients(VanillaTypes.ITEM_STACK, new ArrayList<>(recipe.input.getItems()));
        ItemStack out = new ItemStack(recipe.result);
        if (!out.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 27, 34).addIngredient(VanillaTypes.ITEM_STACK, out).setBackground(guiHelper.getSlotDrawable(), -1,-1);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT,  57, 1).setFluidRenderer(20, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(ForgeTypes.FLUID_STACK, recipe.output);

    }

    @Override
    public void draw(FluidExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Minecraft.getInstance().font.draw(stack, ChatFormatting.DARK_GRAY + "Production: ", 80, 6, 0xFFFFFF);
        Minecraft.getInstance().font.draw(stack, ChatFormatting.DARK_GRAY + "" + recipe.output.getAmount() + "mb/work", 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 1, 0xFFFFFF);
        Minecraft.getInstance().font.draw(stack, ChatFormatting.DARK_GRAY + "" + "Tripled when", 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 2, 0xFFFFFF);
        Minecraft.getInstance().font.draw(stack, ChatFormatting.DARK_GRAY + "" +  "powered", 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 0xFFFFFF);
    }

    @Override
    public List<Component> getTooltipStrings(FluidExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 78 && mouseX <= 140 && mouseY >= 5 && mouseY <= 25)
            return Arrays.asList(new TextComponent("Production rate"));
        return new ArrayList<>();
    }
}
