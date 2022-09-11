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

import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StoneWorkCategory implements IRecipeCategory<StoneWorkCategory.Wrapper> {
    private final IGuiHelper helper;

    public StoneWorkCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<Wrapper> getRecipeType() {
        return IndustrialRecipeTypes.STONE_WORK;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY.getLeft().get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 94, 0, 160, 26);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Wrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredient(VanillaTypes.ITEM_STACK, recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 5).addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutput());
        for (int i = 0; i < recipe.getModes().size(); i++) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 29 + i * 24, 5).addIngredient(VanillaTypes.ITEM_STACK, recipe.getModes().get(i).getIcon());
        }
    }

    public static class Wrapper {

        private final ItemStack input;
        private final List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes;
        private final ItemStack output;

        public Wrapper(ItemStack input, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes, ItemStack output) {
            this.input = input;
            this.modes = modes;
            this.output = output;
            while (this.modes.size() < 4) {
                this.modes.add(MaterialStoneWorkFactoryTile.ACTION_RECIPES[4]);
            }
        }

        public ItemStack getInput() {
            return input;
        }

        public List<MaterialStoneWorkFactoryTile.StoneWorkAction> getModes() {
            return modes;
        }

        public ItemStack getOutput() {
            return output;
        }
    }
}
