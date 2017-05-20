package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.agriculture.CropSowerTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

public class CropSowerFilterInfoPiece extends BasicRenderedGuiPiece {

    private CropSowerTile tile;

    public CropSowerFilterInfoPiece(CropSowerTile tile, int left, int top) {
        super(left, top, 67, 67, ClientProxy.GUI, 1, 72);
        this.tile = tile;
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        int i = 0;
        for (ItemStack stack : tile.getFilterStorage()) {
            if (stack != null && !stack.isEmpty()) {
                ItemStackUtils.renderItemIntoGUI(stack, -18 * 4 + 14 + 18 * (i % 3), 26 + (18 * (i / 3)), 6);
            }
            ++i;
        }
    }
}
