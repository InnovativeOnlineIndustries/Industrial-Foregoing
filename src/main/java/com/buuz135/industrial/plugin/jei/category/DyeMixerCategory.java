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

import com.buuz135.industrial.block.resourceproduction.tile.DyeMixerTile;
import com.buuz135.industrial.config.machine.core.LatexProcessingUnitConfig;
import com.buuz135.industrial.config.machine.resourceproduction.DyeMixerConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.util.AssetUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DyeMixerCategory implements IRecipeCategory<DyeMixerCategory.Recipe> {
    public record Recipe(int red, int green, int blue, int dye) {}
    public static final RecipeType<Recipe> RECIPE_TYPE = RecipeType.create(Reference.MOD_ID, "dummy/dye_mixer", Recipe.class);

    private final IGuiHelper helper;
    private final IDrawable bigTank;

    public DyeMixerCategory(IGuiHelper helper) {
        this.helper = helper;
        this.bigTank = helper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3, 12, 50);
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModuleResourceProduction.DYE_MIXER.getBlock().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(112, 60);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        ItemStack dye = new ItemStack(DyeItem.byColor(DyeColor.byId(recipe.dye)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).addIngredient(VanillaTypes.ITEM_STACK, dye);
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND), 0, 3);
        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL), 64, 22);

        SlotsScreenAddon.drawAsset(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 92, 5, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);

        var red = new ProgressBarScreenAddon<DyeMixerTile>(20, 3, new ProgressBarComponent<>(0, 0, 3)) {
            @Override
            public List<Component> getTooltipLines() {
                return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(recipe.red) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
            }
        };
        red.getProgressBar().setColor(DyeColor.RED);
        red.getProgressBar().setProgress(recipe.red);
        var green = new ProgressBarScreenAddon<DyeMixerTile>(20 + 13, 3, new ProgressBarComponent<>(0, 0, 3)) {
            @Override
            public List<Component> getTooltipLines() {
                return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(recipe.green) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
            }
        };
        green.getProgressBar().setColor(DyeColor.GREEN);
        green.getProgressBar().setProgress(recipe.green);
        var blue = new ProgressBarScreenAddon<DyeMixerTile>(20 + 13 + 13, 3, new ProgressBarComponent<>(0, 0, 3)) {
            @Override
            public List<Component> getTooltipLines() {
                return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(recipe.blue) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
            }
        };
        blue.getProgressBar().setColor(DyeColor.BLUE);
        blue.getProgressBar().setProgress(recipe.blue);

        red.drawBackgroundLayer(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 0, 0, 0, 3);
        green.drawBackgroundLayer(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 0, 0, 0, 3);
        blue.drawBackgroundLayer(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 0, 0, 0, 3);

        int consumed = DyeMixerConfig.powerPerTick * 100;
        EnergyBarScreenAddon.drawForeground(guiGraphics, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 3, 0, 0, consumed, Math.max(DyeMixerConfig.maxStoredPower, consumed));
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, Recipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Rectangle rec = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND).getArea();
        if (new Rectangle(0, 3, rec.width, rec.height).contains(mouseX, mouseY)) {
            int consumed = DyeMixerConfig.powerPerTick * 100;
            tooltip.addAll(EnergyBarScreenAddon.getTooltip(consumed, Math.max(LatexProcessingUnitConfig.maxStoredPower, consumed)));
        }

        Rectangle prog = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.PROGRESS_BAR_BORDER_VERTICAL).getArea();
        if (new Rectangle(20, 3, prog.width, prog.height).contains(mouseX, mouseY)) {
            tooltip.addAll(new ProgressBarScreenAddon<DyeMixerTile>(20, 3, new ProgressBarComponent<>(0, 0, 300)) {
                @Override
                public List<Component> getTooltipLines() {
                    return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(recipe.red) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
                }
            }.getTooltipLines());
        }
        if (new Rectangle(20 + 13, 3, prog.width, prog.height).contains(mouseX, mouseY)) {
            tooltip.addAll(new ProgressBarScreenAddon<DyeMixerTile>(20 + 13, 3, new ProgressBarComponent<>(0, 0, 300)) {
                @Override
                public List<Component> getTooltipLines() {
                    return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(recipe.green) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
                }
            }.getTooltipLines());
        }
        if (new Rectangle(20 + 13 + 13, 3, prog.width, prog.height).contains(mouseX, mouseY)) {
            tooltip.addAll(new ProgressBarScreenAddon<DyeMixerTile>(20 + 13 + 13, 3, new ProgressBarComponent<>(0, 0, 300)) {
                @Override
                public List<Component> getTooltipLines() {
                    return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(recipe.blue) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
                }
            }.getTooltipLines());
        }
    }
}
