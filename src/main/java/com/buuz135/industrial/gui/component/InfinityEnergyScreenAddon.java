package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon.drawBackground;
import static com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon.drawForeground;

public class InfinityEnergyScreenAddon extends BasicScreenAddon {

    private final InfinityEnergyStorage handler;
    private final Pair<InfinityTier, InfinityTier> tier;
    private IAsset background;

    public InfinityEnergyScreenAddon(int posX, int posY, InfinityEnergyStorage handler) {
        super(posX, posY);
        this.handler = handler;
        this.tier = InfinityTier.getTierBraquet(handler.getLongEnergyStored());
    }

    public static java.util.List<ITextComponent> getTooltip(long stored, long capacity) {
        return Arrays.asList(new StringTextComponent(TextFormatting.GOLD + "Power:"), new StringTextComponent(new DecimalFormat().format(stored) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(capacity) + TextFormatting.DARK_AQUA + " FE"));
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
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        background = drawBackground(stack, screen, provider, getPosX(), getPosY(), guiX, guiY);
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {
        drawForeground(stack, screen, provider, getPosX(), getPosY(), guiX, guiY, handler.getLongEnergyStored(), tier.getRight().getPowerNeeded());
    }

    @Override
    public List<ITextComponent> getTooltipLines() {
        return getTooltip(handler.getLongEnergyStored(), tier.getRight().getPowerNeeded());
    }
}