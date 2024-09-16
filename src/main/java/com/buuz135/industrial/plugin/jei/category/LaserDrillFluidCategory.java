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

import com.buuz135.industrial.plugin.jei.IndustrialRecipeTypes;
import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;

public class LaserDrillFluidCategory implements IRecipeCategory<LaserDrillFluidRecipe> {

    private final IDrawableStatic smallTank;
    private IGuiHelper guiHelper;

    public LaserDrillFluidCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.smallTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 235 + 3, 1 + 3, 12, 13);
    }


    @Override
    public RecipeType<LaserDrillFluidRecipe> getRecipeType() {
        return IndustrialRecipeTypes.LASER_FLUID;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Laser Drill Fluids");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0, 52, 26).addPadding(0, 60, 35, 65).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserDrillFluidRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 36, 5).addIngredients(recipe.catalyst);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 60 + 35 + 6, 6).setFluidRenderer(200, false, 12, 13).setOverlay(smallTank, 0, 0).addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output);

    }

    @Override
    public void draw(LaserDrillFluidRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int recipeWidth = 82 + 35 + 35;
        if (recipe.pointer > 0)
            AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.BUTTON_ARROW_LEFT), 0, 70);
        if (recipe.pointer < recipe.rarity.size() - 1)
            AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.BUTTON_ARROW_RIGHT), 137, 70);
        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 60 + 35 + 3, 3);
        var toasts = ResourceLocation.fromNamespaceAndPath("minecraft", "toast/tree");
        guiGraphics.blitSprite(toasts, recipeWidth / 10 * 2, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 20, 20);
        guiGraphics.blitSprite(toasts, recipeWidth / 10 * 7, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 20, 20);
        var icons = ResourceLocation.fromNamespaceAndPath("neoforge", "textures/gui/icons.png");
        guiGraphics.blit(icons, recipeWidth / 10 * 7 + 1, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 3, 0, 16, 16, 16);

        String minY = Component.translatable("text.industrialforegoing.miny").getString() + " " + recipe.rarity.get(recipe.pointer).depth_min();
        String maxY = Component.translatable("text.industrialforegoing.maxy").getString() + " " + recipe.rarity.get(recipe.pointer).depth_max();
        String biomes = Component.translatable("text.industrialforegoing.requirements").getString();

        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + minY, recipeWidth / 10, 30, 0, false);
        if (!LaserDrillFluidRecipe.EMPTY.equals(recipe.entity)) {
            String wight = "Over: " + Component.translatable("entity." + recipe.entity.toString().replace(":", ".")).getString();
            guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + wight, recipeWidth / 10, 30 + (Minecraft.getInstance().font.lineHeight + 2), 0, false);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + maxY, recipeWidth / 10 * 6, 30, 0, false);
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + "" + ChatFormatting.UNDERLINE + biomes, recipeWidth / 2 - Minecraft.getInstance().font.width(biomes) / 2, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 2, 0, false);

    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, LaserDrillFluidRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && recipe.pointer > 0) { // Inside the back button
            tooltip.add(Component.translatable("text.industrialforegoing.button.jei.prev_rarity"));
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && recipe.pointer < recipe.rarity.size() - 1) { //Inside the next button
            tooltip.add(Component.translatable("text.industrialforegoing.button.jei.next_rarity"));
        }
        if (mouseX > 13 * 2 && mouseX < 13 * 2 + 20 && mouseY > 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 20) { //Inside the whitelisted biomes
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.whitelisted_dimensions").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).dimensionRarity().whitelist().isEmpty())
                tooltip.add(Component.literal("- Any"));
            else {
                for (ResourceKey<DimensionType> registryKey : recipe.rarity.get(recipe.pointer).dimensionRarity().whitelist()) {
                    tooltip.add(Component.literal("- " + WordUtils.capitalize(Arrays.stream(registryKey.location().getPath().split("_")).reduce((string, string2) -> string + " " + string2).get())));
                }
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.whitelisted_biomes").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).biomeRarity().whitelist().isEmpty())
                tooltip.add(Component.literal("- Any"));
            else {
                for (TagKey<Biome> registryKey : recipe.rarity.get(recipe.pointer).biomeRarity().whitelist()) {
                    for (Holder<Biome> biomeHolder : Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME).getTagOrEmpty(registryKey)) {
                        tooltip.add(Component.literal("- ").append(Component.translatable("biome." + biomeHolder.getKey().location().getNamespace() + "." + biomeHolder.getKey().location().getPath())));
                    }
                }
            }
        }
        if (mouseX > 13 * 8 && mouseX < 13 * 8 + 20 && mouseY > 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 20) { //Inside the whitelisted biomes
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.blacklisted_dimensions").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).dimensionRarity().blacklist().isEmpty())
                tooltip.add(Component.literal("- None"));
            else {
                for (ResourceKey<DimensionType> registryKey : recipe.rarity.get(recipe.pointer).dimensionRarity().blacklist()) {
                    tooltip.add(Component.literal("- " + WordUtils.capitalize(Arrays.stream(registryKey.location().getPath().split("_")).reduce((string, string2) -> string + " " + string2).get())));
                }
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.blacklisted_biomes").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).biomeRarity().blacklist().isEmpty())
                tooltip.add(Component.literal("- None"));
            else {
                for (TagKey<Biome> registryKey : recipe.rarity.get(recipe.pointer).biomeRarity().blacklist()) {
                    for (Holder<Biome> biomeHolder : Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME).getTagOrEmpty(registryKey)) {
                        tooltip.add(Component.literal("- ").append(Component.translatable("biome." + biomeHolder.getKey().location().getNamespace() + "." + biomeHolder.getKey().location().getPath())));
                    }
                }
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, LaserDrillFluidRecipe recipe, IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        builder.addInputHandler(new LaserDrillOreCategory.ClickHandler<LaserDrillFluidRecipe>(LaserDrillOreCategory.LEFT, recipe, (mouseX, mouseY, iJeiUserInput) -> {
            if (recipe.pointer > 0) {
                --recipe.pointer;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }));
        builder.addInputHandler(new LaserDrillOreCategory.ClickHandler<LaserDrillFluidRecipe>(LaserDrillOreCategory.RIGHT, recipe, (mouseX, mouseY, iJeiUserInput) -> {
            if (recipe.pointer < recipe.rarity.size() - 1) {
                ++recipe.pointer;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }));
    }

}
