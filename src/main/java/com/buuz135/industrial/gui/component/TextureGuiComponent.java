package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.api.conveyor.gui.PositionedGuiComponent;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TextureGuiComponent extends PositionedGuiComponent {

    private final ResourceLocation resourceLocation;
    private final int textureX;
    private final int textureY;
    private final List<String> tooltip;

    public TextureGuiComponent(int x, int y, int xSize, int ySize, ResourceLocation resourceLocation, int textureX, int textureY, String... tooltip) {
        super(x, y, xSize, ySize);
        this.resourceLocation = resourceLocation;
        this.textureX = textureX;
        this.textureY = textureY;
        this.tooltip = new ArrayList<>();
        if (tooltip != null) {
            for (int i = 0; i < tooltip.length; i++) {
                this.tooltip.add(new TextComponentTranslation("conveyor.upgrade.industrialforegoing.tooltip." + tooltip[i]).getFormattedText());
            }
        }
    }

    @Override
    public void handleClick(GuiConveyor conveyor, int guiX, int guiY, int mouseX, int mouseY) {

    }

    @Override
    public void drawGuiBackgroundLayer(int guiX, int guiY, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        Minecraft.getMinecraft().currentScreen.drawTexturedModalRect(guiX + getXPos(), guiY + getYPos(), textureX, textureY, getXSize(), getYSize());
    }

    @Override
    public void drawGuiForegroundLayer(int guiX, int guiY, int mouseX, int mouseY) {

    }

    @Nullable
    @Override
    public List<String> getTooltip(int guiX, int guiY, int mouseX, int mouseY) {
        return tooltip;
    }
}
