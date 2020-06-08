package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class InfinityEnergyScreenAddon extends BasicScreenAddon {

    private final InfinityEnergyStorage handler;
    private final Pair<InfinityTier, InfinityTier> tier;
    private IAsset background;

    public InfinityEnergyScreenAddon(int posX, int posY, InfinityEnergyStorage handler) {
        super(posX, posY);
        this.handler = handler;
        this.tier = InfinityTier.getTierBraquet(handler.getLongEnergyStored());
    }

    public static IAsset drawBackground(Screen screen, IAssetProvider provider, int handlerPosX, int handlerPosY, int guiX, int guiY) {
        IAsset background = IAssetProvider.getAsset(provider, AssetTypes.ENERGY_BACKGROUND);
        Point offset = background.getOffset();
        Rectangle area = background.getArea();
        AssetUtil.drawAsset(screen, background, guiX + handlerPosX + offset.x, guiY + handlerPosY + offset.y);
        return background;
    }

    public static void drawForeground(Screen screen, IAssetProvider provider, int handlerPosX, int handlerPosY, int guiX, int guiY, long stored, long capacity) {
        IAsset asset = IAssetProvider.getAsset(provider, AssetTypes.ENERGY_BAR);
        Point offset = asset.getOffset();
        Rectangle area = asset.getArea();
        screen.getMinecraft().getTextureManager().bindTexture(asset.getResourceLocation());
        int powerOffset = (int) (stored * area.height / Math.max(capacity, 1));
        screen.blit(handlerPosX + offset.x, handlerPosY + offset.y + area.height - powerOffset, area.x, area.y + (area.height - powerOffset), area.width, powerOffset);
    }

    public static java.util.List<String> getTooltip(long stored, long capacity) {
        return Arrays.asList(TextFormatting.GOLD + "Power:", new DecimalFormat().format(stored) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(capacity) + TextFormatting.DARK_AQUA + " FE");
    }

    @Override
    public int getXSize() {
        return background != null ? background.getArea().width : 0;
    }

    @Override
    public int getYSize() {
        return background != null ? background.getArea().height : 0;
    }

    @Override
    public void drawBackgroundLayer(Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        background = drawBackground(screen, provider, getPosX(), getPosY(), guiX, guiY);
    }

    @Override
    public void drawForegroundLayer(Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {
        drawForeground(screen, provider, getPosX(), getPosY(), guiX, guiY, handler.getLongEnergyStored(), tier.getRight().getPowerNeeded());
    }

    @Override
    public List<String> getTooltipLines() {
        return getTooltip(handler.getLongEnergyStored(), tier.getRight().getPowerNeeded());
    }
}