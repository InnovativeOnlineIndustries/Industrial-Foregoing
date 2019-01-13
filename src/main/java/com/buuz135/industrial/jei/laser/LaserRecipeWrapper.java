/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.jei.laser;

import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaserRecipeWrapper implements IRecipeWrapper {

    private final LaserDrillEntry.LaserDrillEntryExtended entryExtended;
    private int pointer = 0;

    public LaserRecipeWrapper(LaserDrillEntry.LaserDrillEntryExtended entryExtended) {
        this.entryExtended = entryExtended;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(ItemRegistry.laserLensItem, 1, entryExtended.getLaserMeta()));
        ingredients.setOutput(ItemStack.class, entryExtended.getStack());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"));
        if (pointer > 0) Gui.drawModalRectWithCustomSizedTexture(0, 70, 17, 56, 15, 15, 256, 256);
        if (pointer < entryExtended.getRarities().size() - 1)
            Gui.drawModalRectWithCustomSizedTexture(137, 70, 17 + 16, 56, 15, 15, 256, 256);
        minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/gui/toasts.png"));
        Gui.drawModalRectWithCustomSizedTexture(recipeWidth / 10 * 2, 30 + (minecraft.fontRenderer.FONT_HEIGHT + 2) * 3, 216, 0, 20, 20, 256, 256);
        Gui.drawModalRectWithCustomSizedTexture(recipeWidth / 10 * 7, 30 + (minecraft.fontRenderer.FONT_HEIGHT + 2) * 3, 216, 0, 20, 20, 256, 256);
        minecraft.getRenderItem().renderItemIntoGUI(new ItemStack(Blocks.BARRIER), recipeWidth / 10 * 7 + 1, 30 + (minecraft.fontRenderer.FONT_HEIGHT + 2) * 3 + 3);

        String minY = new TextComponentTranslation("text.industrialforegoing.miny").getUnformattedComponentText() + " " + entryExtended.getRarities().get(pointer).getMinY();
        String maxY = new TextComponentTranslation("text.industrialforegoing.maxy").getUnformattedComponentText() + " " + entryExtended.getRarities().get(pointer).getMaxY();
        String wight = new TextComponentTranslation("text.industrialforegoing.weight").getUnformattedComponentText() + " " + entryExtended.getRarities().get(pointer).getWeight();
        String biomes = new TextComponentTranslation("text.industrialforegoing.biomes").getUnformattedComponentText();
        minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + minY, recipeWidth / 10, 30, 0);
        minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + wight, recipeWidth / 10, 30 + (minecraft.fontRenderer.FONT_HEIGHT + 2), 0);
        minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + maxY, recipeWidth / 10 * 6, 30, 0);
        minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + "" + TextFormatting.UNDERLINE + biomes, recipeWidth / 2 - minecraft.fontRenderer.getStringWidth(biomes) / 2, 30 + (minecraft.fontRenderer.FONT_HEIGHT + 2) * 2, 0);


    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && pointer > 0) { // Inside the back button
            return Arrays.asList(new TextComponentTranslation("text.industrialforegoing.button.jei.prev_rarity").getUnformattedComponentText());
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && pointer < entryExtended.getRarities().size() - 1) { //Inside the next button
            return Arrays.asList(new TextComponentTranslation("text.industrialforegoing.button.jei.next_rarity").getUnformattedComponentText());
        }
        if (mouseX > 13 * 2 && mouseX < 13 * 2 + 20 && mouseY > 30 + (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2) * 3 && mouseY < 30 + (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2) * 3 + 20) { //Inside the whitelisted biomes
            List<String> biomes = new ArrayList<>();
            biomes.add(TextFormatting.UNDERLINE + new TextComponentTranslation("text.industrialforegoing.tooltip.whitelisted_biomes").getUnformattedText());
            if (entryExtended.getRarities().get(pointer).getWhitelist().isEmpty()) biomes.add("- Any");
            else {
                entryExtended.getRarities().get(pointer).getWhitelist().forEach(biome -> biomes.add("- " + biome.getBiomeName()));
            }
            return biomes;
        }
        if (mouseX > 13 * 8 && mouseX < 13 * 8 + 20 && mouseY > 30 + (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2) * 3 && mouseY < 30 + (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2) * 3 + 20) { //Inside the whitelisted biomes
            List<String> biomes = new ArrayList<>();
            biomes.add(TextFormatting.UNDERLINE + new TextComponentTranslation("text.industrialforegoing.tooltip.blacklisted_biomes").getUnformattedText());
            if (entryExtended.getRarities().get(pointer).getBlacklist().isEmpty()) biomes.add("- None");
            else {
                entryExtended.getRarities().get(pointer).getBlacklist().forEach(biome -> biomes.add("- " + biome.getBiomeName()));
            }
            return biomes;
        }
        return null;
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && pointer > 0) {
            --pointer;
            return true;
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && pointer < entryExtended.getRarities().size() - 1) {
            ++pointer;
            return true;
        }
        return false;
    }
}
