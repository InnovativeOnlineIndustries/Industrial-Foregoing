package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.block.transportstorage.tile.PowerCrystalTile;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class PowerCrystalGuiAddon extends BasicScreenAddon {

    private final PowerCrystalTile tile;

    public PowerCrystalGuiAddon(PowerCrystalTile tile, int posX, int posY) {
        super(posX, posY);
        this.tile = tile;
    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        var distance = (int) tile.getCachedDistance();
        var maxDistance = tile.getBase().getMaxDistance();
        var distanceText = "Distance Used: ";
        guiGraphics.drawString(screen.getMinecraft().font, ChatFormatting.DARK_GRAY + distanceText
                + ChatFormatting.DARK_GRAY + getColor(tile.getCachedDistance() / (double) tile.getBase().getMaxDistance()) +
                distance + ChatFormatting.DARK_GRAY + "/" + ChatFormatting.DARK_GRAY + maxDistance + ChatFormatting.DARK_GRAY + " blocks", guiX + getPosX(), guiY + getPosY(), 0xffffff, false);
        guiGraphics.drawString(screen.getMinecraft().font, ChatFormatting.DARK_GRAY + "Mode: " + (tile.isExtracting() ? ChatFormatting.BLUE + "Extracting" : ChatFormatting.GOLD + "Inserting"), guiX + getPosX() + 16, guiY + getPosY() + 16, 0xffffff, false);
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }

    private ChatFormatting getColor(double distance) {
        if (distance >= 0.66) return ChatFormatting.DARK_RED;
        if (distance >= 0.33) return ChatFormatting.GOLD;
        return ChatFormatting.DARK_GREEN;
    }
}
