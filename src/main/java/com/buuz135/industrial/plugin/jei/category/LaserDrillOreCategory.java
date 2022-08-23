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
import java.util.Collections;
import java.util.List;

import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.recipe.LaserDrillOreRecipe;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.Biome;

public class LaserDrillOreCategory implements IRecipeCategory<LaserDrillOreRecipe> {
    private IGuiHelper guiHelper;

    public LaserDrillOreCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return IndustrialRecipeTypes.LASER_ORE.getUid();
    }

    @Override
    public Class<? extends LaserDrillOreRecipe> getRecipeClass() {
        return IndustrialRecipeTypes.LASER_ORE.getRecipeClass();
    }

    @Override
    public Component getTitle() {
        return new TextComponent("Laser Drill Items");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0, 82, 26).addPadding(0, 60, 35, 35).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public RecipeType<LaserDrillOreRecipe> getRecipeType() {
        return IndustrialRecipeTypes.LASER_ORE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserDrillOreRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 36, 5).addIngredients(recipe.catalyst);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 60 + 36, 5).addIngredients(recipe.output);
    }

    @Override
    public void draw(LaserDrillOreRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrix, double mouseX, double mouseY) {
        int recipeWidth = 82 + 35 + 35;
        if (recipe.pointer > 0)
            AssetUtil.drawAsset(matrix, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.BUTTON_ARROW_LEFT), 0, 70);
        if (recipe.pointer < recipe.rarity.length - 1)
            AssetUtil.drawAsset(matrix, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.BUTTON_ARROW_RIGHT), 137, 70);
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/toasts.png"));
        Minecraft.getInstance().screen.blit(matrix, recipeWidth / 10 * 2, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 216, 0, 20, 20, 256, 256);
        Minecraft.getInstance().screen.blit(matrix, recipeWidth / 10 * 7, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 216, 0, 20, 20, 256, 256);
        RenderSystem.setShaderTexture(0, new ResourceLocation("forge", "textures/gui/icons.png"));
        Minecraft.getInstance().screen.blit(matrix, recipeWidth / 10 * 7 + 1, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 3, 0, 16, 16, 16);

        String minY = new TranslatableComponent("text.industrialforegoing.miny").getString() + " " + recipe.rarity[recipe.pointer].depth_min;
        String maxY = new TranslatableComponent("text.industrialforegoing.maxy").getString() + " " + recipe.rarity[recipe.pointer].depth_max;
        String wight = new TranslatableComponent("text.industrialforegoing.weight").getString() + " " + recipe.rarity[recipe.pointer].weight;
        String biomes = new TranslatableComponent("text.industrialforegoing.biomes").getString();
        Minecraft.getInstance().font.draw(matrix, ChatFormatting.DARK_GRAY + minY, recipeWidth / 10, 30, 0);
        Minecraft.getInstance().font.draw(matrix, ChatFormatting.DARK_GRAY + wight, recipeWidth / 10, 30 + (Minecraft.getInstance().font.lineHeight + 2), 0);
        Minecraft.getInstance().font.draw(matrix, ChatFormatting.DARK_GRAY + maxY, recipeWidth / 10 * 6, 30, 0);
        Minecraft.getInstance().font.draw(matrix, ChatFormatting.DARK_GRAY + "" + ChatFormatting.UNDERLINE + biomes, recipeWidth / 2 - Minecraft.getInstance().font.width(biomes) / 2, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 2, 0);
    }

    @Override
    public List<Component> getTooltipStrings(LaserDrillOreRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && recipe.pointer > 0) { // Inside the back button
            return Collections.singletonList(new TranslatableComponent("text.industrialforegoing.button.jei.prev_rarity"));
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && recipe.pointer < recipe.rarity.length - 1) { //Inside the next button
            return Collections.singletonList(new TranslatableComponent("text.industrialforegoing.button.jei.next_rarity"));
        }
        if (mouseX > 13 * 2 && mouseX < 13 * 2 + 20 && mouseY > 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 20) { //Inside the whitelisted biomes
            List<Component> biomes = new ArrayList<>();
            biomes.add(new TranslatableComponent("text.industrialforegoing.tooltip.whitelisted_biomes").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity[recipe.pointer].whitelist.length == 0) biomes.add(new TextComponent("- Any"));
            else {
                for (ResourceKey<Biome> registryKey : recipe.rarity[recipe.pointer].whitelist) {
                    biomes.add(new TextComponent("- ").append(new TranslatableComponent("biome." + registryKey.location().getNamespace() + "." + registryKey.location().getPath())));
                }
            }
            return biomes;
        }
        if (mouseX > 13 * 8 && mouseX < 13 * 8 + 20 && mouseY > 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 20) { //Inside the whitelisted biomes
            List<Component> biomes = new ArrayList<>();
            biomes.add(new TranslatableComponent("text.industrialforegoing.tooltip.blacklisted_biomes").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity[recipe.pointer].blacklist.length == 0) biomes.add(new TextComponent("- None"));
            else {
                for (ResourceKey<Biome> registryKey : recipe.rarity[recipe.pointer].blacklist) {
                    biomes.add(new TextComponent("- ").append(new TranslatableComponent("biome." + registryKey.location().getNamespace() + "." + registryKey.location().getPath())));
                }
            }
            return biomes;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean handleInput(LaserDrillOreRecipe recipe, double mouseX, double mouseY, InputConstants.Key input) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && recipe.pointer > 0) {
            --recipe.pointer;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && recipe.pointer < recipe.rarity.length - 1) {
            ++recipe.pointer;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return false;
    }
}
