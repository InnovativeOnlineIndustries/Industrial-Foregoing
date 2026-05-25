package com.buuz135.industrial.plugin.emi.widget;

import com.buuz135.industrial.plugin.emi.EmiDrawableWidget;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TankWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.awt.*;
import java.util.List;

public class NormalTankEmiWidget extends EmiDrawableWidget {
    private final int x;
    private final int y;
    private final SlotWidget widget;

    public static final Rectangle BOUNDS = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL).getArea();

    public NormalTankEmiWidget(int x, int y, EmiStack stack, long capacity, EmiRecipe recipeContext) {
        this.x = x;
        this.y = y;

        this.widget = new TankWidget(stack, x + 2, y + 2, 14, 52, capacity).drawBack(false).recipeContext(recipeContext);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        this.widget.render(draw, mouseX, mouseY, delta);
        AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), this.x, this.y);
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(this.x, this.y, BOUNDS.width, BOUNDS.height);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return this.widget.getTooltip(mouseX, mouseY);
    }
}
