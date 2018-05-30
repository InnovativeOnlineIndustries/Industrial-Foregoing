package com.buuz135.industrial.gui.component;


import com.buuz135.industrial.api.conveyor.gui.PositionedGuiComponent;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import net.minecraft.client.Minecraft;

public abstract class TextGuiComponent extends PositionedGuiComponent {

    public TextGuiComponent(int x, int y) {
        super(x, y, 0, 0);
    }

    @Override
    public void handleClick(GuiConveyor conveyor, int guiX, int guiY, int mouseX, int mouseY) {

    }

    @Override
    public void drawGuiBackgroundLayer(int guiX, int guiY, int mouseX, int mouseY) {
        Minecraft.getMinecraft().fontRenderer.drawString(getText(), guiX + getXPos(), guiY + getYPos(), 0xffffff);
    }

    @Override
    public void drawGuiForegroundLayer(int guiX, int guiY, int mouseX, int mouseY) {

    }

    public abstract String getText();
}
