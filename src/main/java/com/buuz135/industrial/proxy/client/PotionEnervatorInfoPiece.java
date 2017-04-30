package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.tile.PotionEnervatorTile;
import net.minecraft.util.ResourceLocation;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;

public class PotionEnervatorInfoPiece extends BasicRenderedGuiPiece {

    private PotionEnervatorTile tile;

    public PotionEnervatorInfoPiece(PotionEnervatorTile tile, int left, int top, int width, int height, ResourceLocation texture, int textureX, int textureY) {
        super(left, top, width, height, texture, textureX, textureY);
        this.tile = tile;
    }
}
