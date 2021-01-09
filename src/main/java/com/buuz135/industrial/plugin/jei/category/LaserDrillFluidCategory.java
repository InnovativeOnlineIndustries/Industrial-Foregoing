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

import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
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
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LaserDrillFluidCategory implements IRecipeCategory<LaserDrillFluidRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(LaserDrillFluidRecipe.SERIALIZER.getRecipeType().toString());

    private IGuiHelper guiHelper;

    public LaserDrillFluidCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends LaserDrillFluidRecipe> getRecipeClass() {
        return LaserDrillFluidRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Laser Drill Fluids";
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0, 52, 26).addPadding(0, 60, 35, 65).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(LaserDrillFluidRecipe laserDrillOreRecipe, IIngredients iIngredients) {
        iIngredients.setInputs(VanillaTypes.ITEM, Arrays.asList(laserDrillOreRecipe.catalyst.getMatchingStacks()));
        iIngredients.setOutput(VanillaTypes.FLUID, FluidStack.loadFluidStackFromNBT(laserDrillOreRecipe.output));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, LaserDrillFluidRecipe laserDrillOreRecipe, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStackGroup = iRecipeLayout.getItemStacks();
        guiItemStackGroup.init(0, true, 35, 4);
        guiItemStackGroup.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        IGuiFluidStackGroup guiFluidStackGroup = iRecipeLayout.getFluidStacks();
        guiFluidStackGroup.init(1, false, 60 + 35 + 6, 6, 12, 13, 100, false, new IDrawable() {
            @Override
            public int getWidth() {
                return 15;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void draw(MatrixStack matrixStack, int i, int i1) {
                AssetUtil.drawAsset(matrixStack, Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), i - 3, i1 - 3);
            }
        });
        guiFluidStackGroup.set(1, iIngredients.getOutputs(VanillaTypes.FLUID).get(0));
    }

    @Override
    public void draw(LaserDrillFluidRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        int recipeWidth = 82 + 35 + 35;
        if (recipe.pointer > 0)
            AssetUtil.drawAsset(matrixStack, Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.BUTTON_ARROW_LEFT), 0, 70);
        if (recipe.pointer < recipe.rarity.length - 1)
            AssetUtil.drawAsset(matrixStack, Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.BUTTON_ARROW_RIGHT), 137, 70);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("textures/gui/toasts.png"));
        Minecraft.getInstance().currentScreen.blit(matrixStack, recipeWidth / 10 * 2, 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3, 216, 0, 20, 20, 256, 256);
        Minecraft.getInstance().currentScreen.blit(matrixStack, recipeWidth / 10 * 7, 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3, 216, 0, 20, 20, 256, 256);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("forge", "textures/gui/icons.png"));
        Minecraft.getInstance().currentScreen.blit(matrixStack, recipeWidth / 10 * 7 + 1, 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3 + 3, 0, 16, 16, 16);

        String minY = new TranslationTextComponent("text.industrialforegoing.miny").getString() + " " + recipe.rarity[recipe.pointer].depth_min;
        String maxY = new TranslationTextComponent("text.industrialforegoing.maxy").getString() + " " + recipe.rarity[recipe.pointer].depth_max;
        String biomes = new TranslationTextComponent("text.industrialforegoing.biomes").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + minY, recipeWidth / 10, 30, 0);
        if (!LaserDrillFluidRecipe.EMPTY.equals(recipe.entity)){
            String wight = "Over: " + new TranslationTextComponent("entity." + recipe.entity.toString().replace(":", ".")).getString();
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + wight, recipeWidth / 10, 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2), 0);
        }
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + maxY, recipeWidth / 10 * 6, 30, 0);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.DARK_GRAY + "" + TextFormatting.UNDERLINE + biomes, recipeWidth / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(biomes) / 2, 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 2, 0);
    }

    @Override
    public List<ITextComponent> getTooltipStrings(LaserDrillFluidRecipe recipe, double mouseX, double mouseY) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && recipe.pointer > 0) { // Inside the back button
            return Collections.singletonList(new TranslationTextComponent("text.industrialforegoing.button.jei.prev_rarity"));
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && recipe.pointer < recipe.rarity.length - 1) { //Inside the next button
            return Collections.singletonList(new TranslationTextComponent("text.industrialforegoing.button.jei.next_rarity"));
        }
        if (mouseX > 13 * 2 && mouseX < 13 * 2 + 20 && mouseY > 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3 + 20) { //Inside the whitelisted biomes
            List<ITextComponent> biomes = new ArrayList<>();
            biomes.add(new TranslationTextComponent("text.industrialforegoing.tooltip.whitelisted_biomes").mergeStyle(TextFormatting.UNDERLINE).mergeStyle(TextFormatting.GOLD));
            if (recipe.rarity[recipe.pointer].whitelist.length == 0) biomes.add(new StringTextComponent("- Any"));
            else {
                for (RegistryKey<Biome> registryKey : recipe.rarity[recipe.pointer].whitelist) {
                    biomes.add(new StringTextComponent("- ").append(new TranslationTextComponent("biome." + registryKey.getLocation().getNamespace() + "." + registryKey.getLocation().getPath())));
                }
            }
            return biomes;
        }
        if (mouseX > 13 * 8 && mouseX < 13 * 8 + 20 && mouseY > 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2) * 3 + 20) { //Inside the whitelisted biomes
            List<ITextComponent> biomes = new ArrayList<>();
            biomes.add(new TranslationTextComponent("text.industrialforegoing.tooltip.blacklisted_biomes").mergeStyle(TextFormatting.UNDERLINE).mergeStyle(TextFormatting.GOLD));
            if (recipe.rarity[recipe.pointer].blacklist.length == 0) biomes.add(new StringTextComponent("- None"));
            else {
                for (RegistryKey<Biome> registryKey : recipe.rarity[recipe.pointer].blacklist) {
                    biomes.add(new StringTextComponent("- ").append(new TranslationTextComponent("biome." + registryKey.getLocation().getNamespace() + "." + registryKey.getLocation().getPath())));
                }
            }
            return biomes;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(LaserDrillFluidRecipe recipe, double mouseX, double mouseY, int mouseButton) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && recipe.pointer > 0) {
            --recipe.pointer;
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && recipe.pointer < recipe.rarity.length - 1) {
            ++recipe.pointer;
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return false;
    }
}
