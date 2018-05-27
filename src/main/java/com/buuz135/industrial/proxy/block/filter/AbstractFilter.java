package com.buuz135.industrial.proxy.block.filter;

import net.minecraft.entity.Entity;

public abstract class AbstractFilter<T extends Entity> implements IFilter<T> {

    private final int sizeX;
    private final int sizeY;
    private GhostSlot[] filter;

    protected AbstractFilter(int locX, int locY, int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.filter = new GhostSlot[sizeX * sizeY];
        int pos = 0;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                this.filter[pos] = new GhostSlot(pos, locX + (x * 18), locY + y * 18);
                ++pos;
            }
        }
    }

    @Override
    public GhostSlot[] getFilter() {
        return filter;
    }
}
