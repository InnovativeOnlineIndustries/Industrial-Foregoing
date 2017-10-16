package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.generator.AbstractReactorTile;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

import java.text.DecimalFormat;
import java.util.Arrays;

public class BioreactorEfficiencyInfoPiece extends BasicRenderedGuiPiece {

    private AbstractReactorTile tile;

    public BioreactorEfficiencyInfoPiece(AbstractReactorTile tile, int left, int top) {
        super(left, top, 18, 54, ClientProxy.GUI, 93, 72);
        this.tile = tile;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
        double work = tile.getEfficiency();
        container.drawTexturedRect(this.getLeft() + 3, (int) (this.getTop() + 3 + (50 - (50 * work))), 112, (int) (72 + (50 - (50 * work))), 12, (int) (work * 50));

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
        if (isInside(container, mouseX, mouseY)) {
            float eff = this.tile.getEfficiency();
            eff = eff < 0 ? 0 : eff;
            container.drawTooltip(Arrays.asList("Efficiency: " + new DecimalFormat("##.##").format(100 * eff) + "%", "Producing: " + this.tile.getProducedAmountItem() + "mb/item"), mouseX - guiX, mouseY - guiY);
        }
    }
}
