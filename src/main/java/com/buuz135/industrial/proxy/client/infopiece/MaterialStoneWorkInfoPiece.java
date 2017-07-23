package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.ToggleButtonPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.Arrays;

public class MaterialStoneWorkInfoPiece extends ToggleButtonPiece {

    private MaterialStoneWorkFactoryTile tile;
    private int id;

    public MaterialStoneWorkInfoPiece(MaterialStoneWorkFactoryTile tile, int left, int top, int id) {
        super(left, top, 17, 17);
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
            container.drawTooltip(Arrays.asList(tile.getEntry(id).getValue().getName()), this.getLeft() + 8, this.getTop() + 8);
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
