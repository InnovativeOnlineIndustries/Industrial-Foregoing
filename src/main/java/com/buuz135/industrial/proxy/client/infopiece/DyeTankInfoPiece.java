package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.misc.DyeMixerTile;
import net.minecraft.item.EnumDyeColor;
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
            container.drawTooltip(Arrays.asList("Dye amount: " + new DecimalFormat("##.##").format(100 * amount) + "%"), mouseX - guiX, mouseY - guiY);
        }
    }

    private int getColorAmount() {
        return color == EnumDyeColor.RED ? tile.getR() : color == EnumDyeColor.GREEN ? tile.getG() : tile.getB();
    }

    private int getModifier() {
        return color == EnumDyeColor.RED ? 0 : color == EnumDyeColor.GREEN ? 1 : 2;
    }
}
