package com.buuz135.industrial.plugin.emi.widget;

import com.buuz135.industrial.plugin.emi.EmiDrawableWidget;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressBarEmiWidget extends EmiDrawableWidget {
    private final List<ClientTooltipComponent> tooltips;
    private final ProgressBarScreenAddon<?> addon;
    private final Rectangle bounds;

    public ProgressBarEmiWidget(ProgressBarScreenAddon<?> addon, Rectangle bounds) {
        this.addon = addon;
        this.bounds = bounds;

        List<Component> tooltipLines = addon.getTooltipLines();
        this.tooltips = new ArrayList<>(tooltipLines.size());
        for (var line : tooltipLines) {
            this.tooltips.add(ClientTooltipComponent.create(line.getVisualOrderText()));
        }
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        this.addon.drawBackgroundLayer(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 0, 0, 0, 3);
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(this.addon.getPosX(), this.addon.getPosY(), this.bounds.width, this.bounds.height);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return this.tooltips;
    }
}
