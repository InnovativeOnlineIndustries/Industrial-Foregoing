package com.buuz135.industrial.api;


import com.buuz135.industrial.api.plant.IPlantRecollectable;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.api.recipe.SludgeEntry;
import net.minecraft.item.ItemStack;

import java.util.stream.Collectors;

public class IndustrialForegoingHelper {

    public static final String MOD_ID = "industrialforegoing";
    public static final String API_VERSION = "2";
    public static final String API_ID = MOD_ID + "api";

    /**
     * Adds a new BioReactor entry that wasn't already there.
     *
     * @param entry The BioReactor entry to add.
     * @return true if it is added, false if don't.
     */
    public static boolean addBioReactorEntry(BioReactorEntry entry) {
        if (BioReactorEntry.BIO_REACTOR_ENTRIES.stream().noneMatch(entry1 -> entry.doesStackMatch(entry1.getStack()))) {
            BioReactorEntry.BIO_REACTOR_ENTRIES.add(entry);
            return true;
        }
        return false;
    }

    /**
     * Removes a BioReactor entry.
     *
     * @param stack The ItemStack of a BioReactor entry to remove.
     * @return true if it is removed, false if don't.
     */
    public static boolean removeBioReactorEntry(ItemStack stack) {
        if (BioReactorEntry.BIO_REACTOR_ENTRIES.stream().anyMatch(entry -> entry.doesStackMatch(stack))) {
            BioReactorEntry.BIO_REACTOR_ENTRIES = BioReactorEntry.BIO_REACTOR_ENTRIES.stream().filter(entry -> !entry.doesStackMatch(stack)).collect(Collectors.toList());
            return true;
        }
        return false;
    }

    /** Adds a new LaserEntry entry that wasn't already there.
     *
     * @param entry The LaserEntry entry to add.
     * @return true if it is added, false if don't.
     */
    public static boolean addLaserDrillEntry(LaserDrillEntry entry) {
        if (LaserDrillEntry.LASER_DRILL_ENTRIES.stream().noneMatch(entry1 -> entry1.getStack().isItemEqual(entry.getStack()))) {
            LaserDrillEntry.LASER_DRILL_ENTRIES.add(entry);
            return true;
        }
        return false;
    }

    /** Removes a LaserDrill entry.
     *
     * @param stack The ItemStack of a LaserDrill entry to remove.
     * @return true if it is removed, false if don't.
     */
    public static boolean removeLaserDrillEntry(ItemStack stack) {
        if (LaserDrillEntry.LASER_DRILL_ENTRIES.stream().anyMatch(entry -> stack.isItemEqual(entry.getStack()))) {
            LaserDrillEntry.LASER_DRILL_ENTRIES = LaserDrillEntry.LASER_DRILL_ENTRIES.stream().filter(entry -> !entry.getStack().isItemEqual(stack)).collect(Collectors.toList());
        }
        return false;
    }

    /** Adds a new SludgeRefiner entry that wasn't already there.
     *
     * @param entry The SludgeRefiner entry to add.
     * @return true if it is added, false if don't.
     */
    public static boolean addSludgeRefinerEntry(SludgeEntry entry) {
        if (SludgeEntry.SLUDGE_RECIPES.stream().noneMatch(entry1 -> entry1.getStack().isItemEqual(entry.getStack()))) {
            SludgeEntry.SLUDGE_RECIPES.add(entry);
            return true;
        }
        return false;
    }

    /** Removes a SludgeRefiner entry.
     *
     * @param stack The ItemStack of a SludgeRefiner entry to remove.
     * @return true if it is removed, false if don't.
     */
    public static boolean removeSludgeRefinerEntry(ItemStack stack) {
        if (SludgeEntry.SLUDGE_RECIPES.stream().anyMatch(entry -> entry.getStack().isItemEqual(stack))) {
            SludgeEntry.SLUDGE_RECIPES = SludgeEntry.SLUDGE_RECIPES.stream().filter(entry -> !entry.getStack().isItemEqual(stack)).collect(Collectors.toList());
            return true;
        }
        return false;
    }

    /** Registers a new IPlantRecollectable at the start of the list giving it preference over the others that are already there.
     *
     * @param recollectable The IPlantRecollectable to register
     */
    public static void addPlantRecollectable(IPlantRecollectable recollectable) {
        IPlantRecollectable.PLANT_RECOLLECTABLES.add(0, recollectable);
    }
}
