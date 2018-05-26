package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.gui.conveyor.GuiConveyor;

import javax.annotation.Nullable;
import java.util.List;

public interface IGuiComponent {

    int getXPos();

    int getYPos();

    int getXSize();

    int getYSize();

    void handleClick(GuiConveyor conveyor, int guiX, int guiY, int mouseX, int mouseY);

    void drawGuiBackgroundLayer(int guiX, int guiY, int mouseX, int mouseY);

    void drawGuiForegroundLayer(int guiX, int guiY, int mouseX, int mouseY);

    default boolean isInside(int mouseX, int mouseY) {
        return mouseX > getXPos() && mouseX < getXPos() + getXSize() && mouseY > getYPos() && mouseY < getYPos() + getYSize();
    }

    @Nullable
    List<String> getTooltip(int guiX, int guiY, int mouseX, int mouseY);
}
