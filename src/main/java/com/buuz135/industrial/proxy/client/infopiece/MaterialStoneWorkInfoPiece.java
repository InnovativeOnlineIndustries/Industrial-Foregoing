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

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import net.minecraft.util.text.TextComponentTranslation;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.ToggleButtonPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.Arrays;

public class MaterialStoneWorkInfoPiece extends ToggleButtonPiece {

    private MaterialStoneWorkFactoryTile tile;
    private int id;

    public MaterialStoneWorkInfoPiece(MaterialStoneWorkFactoryTile tile, int left, int top, int id) {
        super(left, top, 17, 17, 0);
        this.tile = tile;
        this.id = id;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
        container.drawTexturedRect(this.getLeft() - 1, this.getTop() - 1, 1, 168, 18, 18);
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
        if (super.isInside(container, mouseX, mouseY)) {
            container.drawTooltip(Arrays.asList(new TextComponentTranslation(tile.getEntry(id).getValue().getName()).getUnformattedComponentText()), this.getLeft() + 8, this.getTop() + 8);
        }
    }

    @Override
    protected void renderState(BasicTeslaGuiContainer<?> basicTeslaGuiContainer, int i, BoundingRectangle boundingRectangle) {
        ItemStackUtils.renderItemIntoGUI(tile.getEntry(id).getValue().getItemStack(), this.getLeft(), this.getTop(), 7);
    }

    @Override
    protected void clicked() {
        tile.nextMode(id);
    }
}
