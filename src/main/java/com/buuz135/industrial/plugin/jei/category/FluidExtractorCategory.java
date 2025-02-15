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

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FluidExtractorCategory implements IRecipeCategory<FluidExtractorRecipe> {

    private IGuiHelper guiHelper;
    private IDrawable tankOverlay;

    public FluidExtractorCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.tankOverlay = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jei.png"), 1, 207, 12, 48);
    }

    @Override
    public RecipeType<FluidExtractorRecipe> getRecipeType() {
        return IndustrialRecipeTypes.FLUID_EXTRACTOR;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModuleCore.FLUID_EXTRACTOR.getBlock().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jei.png"), 0, 27, 76, 50).addPadding(0, 0, 0, 74).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidExtractorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 17).addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.input.getItems()));
        ItemStack out = new ItemStack(recipe.result.getBlock());
        if (!out.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 27, 34).addIngredient(VanillaTypes.ITEM_STACK, out).setBackground(guiHelper.getSlotDrawable(), -1, -1);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 1).setFluidRenderer(20, false, 12, 48).setOverlay(tankOverlay, 0, 0).addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output);

    }

    @Override
    public void draw(FluidExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.jei.recipe.production").getString(), 80, 6, 0xFFFFFF, false);
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + "" + recipe.output.getAmount() + Component.translatable("text.industrialforegoing.jei.recipe.mb_work").getString(), 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 1, 0xFFFFFF, false);
        if (recipe.outputsLatex()) {
            guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + "" + Component.translatable("text.industrialforegoing.jei.recipe.tripled_when").getString(), 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 2, 0xFFFFFF, false);
            guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + "" + Component.translatable("text.industrialforegoing.jei.recipe.powered").getString(), 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 0xFFFFFF, false);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        if (mouseX >= 78 && mouseX <= 140 && mouseY >= 5 && mouseY <= 25)
            tooltip.add(Component.literal(Component.translatable("text.industrialforegoing.jei.recipe.production_rate").getString()));
    }
}
