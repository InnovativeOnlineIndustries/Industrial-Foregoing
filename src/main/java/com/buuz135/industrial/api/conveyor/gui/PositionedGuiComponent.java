package com.buuz135.industrial.api.conveyor.gui;

public abstract class PositionedGuiComponent implements IGuiComponent {

    private final int x;
    private final int y;
    private final int xSize;
    private final int ySize;

    public PositionedGuiComponent(int x, int y, int xSize, int ySize) {
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    public int getXPos() {
        return x;
    }

    @Override
    public int getYPos() {
        return y;
    }

    @Override
    public int getXSize() {
        return xSize;
    }

    @Override
    public int getYSize() {
        return ySize;
    }
}
