package com.buuz135.industrial.api.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillEntry {

    public static List<LaserDrillEntry> LASER_DRILL_ENTRIES = new ArrayList<>();

    private int laserMeta;
    private ItemStack stack;
    private int weight;

    /**
     * Represents an entry for the laser drill
     *
     * @param laserMeta The metadata of the lens item
     * @param stack     The ItemStack as an output
     * @param weight    The weight in the global pool of items
     */
    public LaserDrillEntry(int laserMeta, ItemStack stack, int weight) {
        this.laserMeta = laserMeta;
        this.stack = stack;
        this.weight = weight;
    }

    public int getLaserMeta() {
        return laserMeta;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getWeight() {
        return weight;
    }
}
