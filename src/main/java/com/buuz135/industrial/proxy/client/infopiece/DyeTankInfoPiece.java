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
import com.buuz135.industrial.tile.misc.DyeMixerTile;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.text.TextComponentTranslation;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

import java.text.DecimalFormat;
import java.util.Arrays;

public class DyeTankInfoPiece extends BasicRenderedGuiPiece {

    private DyeMixerTile tile;
    private EnumDyeColor color;

    public DyeTankInfoPiece(DyeMixerTile tile, EnumDyeColor color, int left, int top, int textureX, int textureY) {
        super(left, top, 18, 33, ClientProxy.GUI, textureX, textureY);
        this.tile = tile;
        this.color = color;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
        double amount = getColorAmount() / 300D;
        container.drawTexturedRect(this.getLeft() + 3, (int) (this.getTop() + 3 + (27 - (27 * amount))), 125 + 18 * getModifier(), (int) (105 + (27 - (27 * amount))), 12, (int) (amount * 27));
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
        if (isInside(container, mouseX, mouseY)) {
            double amount = getColorAmount() / 300D;
            container.drawTooltip(Arrays.asList(new TextComponentTranslation("text.industrialforegoing.display.dye_amount").getUnformattedComponentText() + " " + new DecimalFormat("##.##").format(100 * amount) + "%"), mouseX - guiX, mouseY - guiY);
        }
    }

    private int getColorAmount() {
        return color == EnumDyeColor.RED ? tile.getR() : color == EnumDyeColor.GREEN ? tile.getG() : tile.getB();
    }

    private int getModifier() {
        return color == EnumDyeColor.RED ? 0 : color == EnumDyeColor.GREEN ? 1 : 2;
    }
}
