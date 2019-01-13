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
package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.tile.mob.VillagerTradeExchangerTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

import java.util.Collections;

public class VillagerTradeExchangerInfoPiece extends BasicRenderedGuiPiece {

    private VillagerTradeExchangerTile tile;

    public VillagerTradeExchangerInfoPiece(VillagerTradeExchangerTile tile) {
        super(52, 22, 82, 26, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0);
        this.tile = tile;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        if (tile.getMerchantRecipes() != null && tile.getMerchantRecipes().size() > tile.getCurrent()) {
            MerchantRecipe currentRecipe = tile.getMerchantRecipes().get(tile.getCurrent());
            ItemStackUtils.renderItemIntoGUI(currentRecipe.getItemToBuy(), guiX + this.getLeft() + 1, guiY + this.getHeight() + 1, 7);
            ItemStackUtils.renderItemIntoGUI(currentRecipe.getItemToSell(), guiX + this.getLeft() + 1 + 59, guiY + this.getHeight() + 1, 7);
        }
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
        if (tile.getMerchantRecipes() != null && tile.getMerchantRecipes().size() > tile.getCurrent()) {
            MerchantRecipe currentRecipe = tile.getMerchantRecipes().get(tile.getCurrent());
            if (mouseX > guiX + this.getLeft() && mouseY > guiY + this.getHeight() && mouseX < guiX + this.getLeft() + 18 && mouseY < guiY + this.getHeight() + 18) {
                container.drawTooltip(Collections.singletonList(currentRecipe.getItemToBuy().getCount() + "x " + currentRecipe.getItemToBuy().getDisplayName()), mouseX - guiX, mouseY - guiY);
            }
            if (mouseX > guiX + this.getLeft() + 1 + 59 && mouseY > guiY + this.getHeight() && mouseX < guiX + this.getLeft() + 1 + 59 + 18 && mouseY < guiY + this.getHeight() + 18) {
                container.drawTooltip(Collections.singletonList(currentRecipe.getItemToSell().getCount() + "x " + currentRecipe.getItemToSell().getDisplayName()), mouseX - guiX, mouseY - guiY);
            }
        }
    }
}
