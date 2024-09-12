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

import com.buuz135.industrial.block.core.tile.DissolutionChamberTile;
import com.buuz135.industrial.config.machine.core.DissolutionChamberConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DissolutionChamberCategory implements IRecipeCategory<DissolutionChamberRecipe> {

    private final IGuiHelper guiHelper;
    private final IDrawable smallTank;
    private final IDrawable bigTank;

    public DissolutionChamberCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.smallTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 235 + 3, 1 + 3, 12, 13);
        this.bigTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3, 12, 50);
    }

    @Override
    public ResourceLocation getUid() {
        return IndustrialRecipeTypes.DISSOLUTION.getUid();
    }

    @Override
    public Class<? extends DissolutionChamberRecipe> getRecipeClass() {
        return IndustrialRecipeTypes.DISSOLUTION.getRecipeClass();
    }

    @Override
    public RecipeType<DissolutionChamberRecipe> getRecipeType() {
        return IndustrialRecipeTypes.DISSOLUTION;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent(ModuleCore.DISSOLUTION_CHAMBER.getLeft().get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return this.guiHelper.createBlankDrawable(160, 82);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DissolutionChamberRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < 8; i++) {
            if (i < recipe.input.length) {
                ItemStack stack = recipe.input[i].getItems().stream().toList().get(0);
                stack.getItem().onCraftedBy(stack, null, null);
                builder.addSlot(RecipeIngredientRole.INPUT, 24 + DissolutionChamberTile.getSlotPos(i).getLeft(), 11 + DissolutionChamberTile.getSlotPos(i).getRight()).addIngredients(Ingredient.of(stack));
            }
        }
        if (recipe.inputFluid != null && !recipe.inputFluid.isEmpty()){
            builder.addSlot(RecipeIngredientRole.INPUT, 33 + 12 + 3, 32 + 3).setFluidRenderer(1000, false, 12, 13).setOverlay(smallTank, 0, 0).addIngredient(ForgeTypes.FLUID_STACK, recipe.inputFluid);
        }
        if (!recipe.output.isEmpty()) {
            ItemStack stack = recipe.output;
            stack.getItem().onCraftedBy(stack, null, null);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 119, 16).addIngredient(VanillaTypes.ITEM_STACK, stack);
        }
        if (recipe.outputFluid != null && !recipe.outputFluid.isEmpty()){
            builder.addSlot(RecipeIngredientRole.OUTPUT, 139 + 3, 14 + 3).setFluidRenderer(1000, false, 12, 50).setOverlay(bigTank, 0, 0).addIngredient(ForgeTypes.FLUID_STACK, recipe.outputFluid);
        }
    }

    @Override
    public void draw(DissolutionChamberRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        EnergyBarScreenAddon.drawBackground(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 12, 0, 0);
        SlotsScreenAddon.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 24, 11, 0, 0, 8, DissolutionChamberTile::getSlotPos, integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.LIGHT_BLUE.getFireworkColor()), integer -> true);
        SlotsScreenAddon.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 119, 16, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true);
        AssetUtil.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 33 + 12, 32);
        AssetUtil.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 139, 14);

        AssetUtil.drawAsset(stack, Minecraft.getInstance().screen, IAssetProvider.getAsset(DefaultAssetProvider.DEFAULT_PROVIDER, AssetTypes.PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL), 92, 41 - 8);

        int consumed = recipe.processingTime * DissolutionChamberConfig.powerPerTick;
        EnergyBarScreenAddon.drawForeground(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 12, 0, 0, consumed, (int) Math.max(50000, Math.ceil(consumed)));

    }

    @Override
    public List<Component> getTooltipStrings(DissolutionChamberRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Rectangle rec = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND).getArea();
        if (new Rectangle(0, 12, rec.width, rec.height).contains(mouseX, mouseY)) {
            int consumed = recipe.processingTime * 60;
            return EnergyBarScreenAddon.getTooltip(consumed, (int) Math.max(50000, Math.ceil(consumed)));
        }
        return new ArrayList<>();
    }
}
