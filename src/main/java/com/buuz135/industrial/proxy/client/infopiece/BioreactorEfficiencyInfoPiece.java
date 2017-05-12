package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.generator.BioReactorTile;
import net.minecraft.client.renderer.GlStateManager;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.Arrays;

public class BioreactorEfficiencyInfoPiece extends BasicRenderedGuiPiece {

    private BioReactorTile tile;

    public BioreactorEfficiencyInfoPiece(BioReactorTile tile,int left, int top) {
        super(left, top, 50, 14, ClientProxy.GUI, 1,140);
        this.tile = tile;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container,guiX,guiY,partialTicks,mouseX,mouseY);
        container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
        container.drawTexturedRect(this.getLeft()+1,this.getTop()+1,1,155, (int) (50*this.tile.getEfficiency()),12);

    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
        if (isInside(container,mouseX,mouseY)){
            float eff = this.tile.getEfficiency();
            eff = eff < 0 ? 0 : eff;
            container.drawTooltip(Arrays.asList("Efficiency: "+new DecimalFormat("##.##").format(100*eff)+"%","Producing: " +this.tile.getProducedAmountItem()+"mb/item"),mouseX-guiX,mouseY-guiY);
        }
    }
}
