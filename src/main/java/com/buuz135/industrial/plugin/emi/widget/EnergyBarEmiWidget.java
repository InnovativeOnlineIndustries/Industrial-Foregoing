package com.buuz135.industrial.plugin.emi.widget;

import com.buuz135.industrial.plugin.emi.EmiDrawableWidget;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.List;

public class EnergyBarEmiWidget extends EmiDrawableWidget {
    private final int x;
    private final int y;
    private final long energy;
    private final long maxEnergy;
    private final List<ClientTooltipComponent> tooltips;

    public static final Rectangle BOUNDS = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND).getArea();

    public EnergyBarEmiWidget(int x, int y, int energy, int maxEnergy) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.maxEnergy = maxEnergy;

        this.tooltips = EnergyBarScreenAddon.getTooltip(energy, Math.max(maxEnergy, energy)).stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList();
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND), x, y);
        EnergyBarScreenAddon.drawForeground(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, x, y, 0, 0, this.energy, Math.max(this.maxEnergy, this.energy));
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(this.x, this.y, BOUNDS.width, BOUNDS.height);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return this.tooltips;
    }
}
