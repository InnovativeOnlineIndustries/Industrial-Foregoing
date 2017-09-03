package com.buuz135.industrial.api;


import com.buuz135.industrial.api.plant.IPlantRecollectable;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.api.recipe.SludgeEntry;
import net.minecraft.item.ItemStack;

import java.util.stream.Collectors;

public class IndustrialForegoingHelper {

    public static final String MOD_ID = "industrialforegoing";
    public static final String API_VERSION = "1";
    public static final String API_ID = MOD_ID + "api";

    public static boolean addBioReactorEntry(BioReactorEntry entry) {
        if (BioReactorEntry.BIO_REACTOR_ENTRIES.stream().noneMatch(entry1 -> entry.doesStackMatch(entry1.getStack()))) {
            BioReactorEntry.BIO_REACTOR_ENTRIES.add(entry);
            return true;
        }
        return false;
    }

    public static boolean removeBioReactorEntry(ItemStack stack) {
        if (BioReactorEntry.BIO_REACTOR_ENTRIES.stream().anyMatch(entry -> entry.doesStackMatch(stack))) {
            BioReactorEntry.BIO_REACTOR_ENTRIES = BioReactorEntry.BIO_REACTOR_ENTRIES.stream().filter(entry -> !entry.doesStackMatch(stack)).collect(Collectors.toList());
            return true;
        }
        return false;
    }

    public static boolean addLaserDrillEntry(LaserDrillEntry entry) {
        if (LaserDrillEntry.LASER_DRILL_ENTRIES.stream().noneMatch(entry1 -> entry1.getStack().isItemEqual(entry.getStack()))) {
            LaserDrillEntry.LASER_DRILL_ENTRIES.add(entry);
            return true;
        }
        return false;
    }

    public static boolean removeLaserDrillEntry(ItemStack stack) {
        if (LaserDrillEntry.LASER_DRILL_ENTRIES.stream().anyMatch(entry -> stack.isItemEqual(entry.getStack()))) {
            LaserDrillEntry.LASER_DRILL_ENTRIES = LaserDrillEntry.LASER_DRILL_ENTRIES.stream().filter(entry -> !entry.getStack().isItemEqual(stack)).collect(Collectors.toList());
        }
        return false;
    }

    public static boolean addSludgeRefinerEntry(SludgeEntry entry) {
        if (SludgeEntry.SLUDGE_RECIPES.stream().noneMatch(entry1 -> entry1.getStack().isItemEqual(entry.getStack()))) {
            SludgeEntry.SLUDGE_RECIPES.add(entry);
            return true;
        }
        return false;
    }

    public static boolean removeSludgeRefinerEntry(ItemStack stack) {
        if (SludgeEntry.SLUDGE_RECIPES.stream().anyMatch(entry -> entry.getStack().isItemEqual(stack))) {
            SludgeEntry.SLUDGE_RECIPES = SludgeEntry.SLUDGE_RECIPES.stream().filter(entry -> !entry.getStack().isItemEqual(stack)).collect(Collectors.toList());
            return true;
        }
        return false;
    }

    public static void addPlantRecollectable(IPlantRecollectable recollectable) {
        IPlantRecollectable.PLANT_RECOLLECTABLES.add(0, recollectable);
    }
}
