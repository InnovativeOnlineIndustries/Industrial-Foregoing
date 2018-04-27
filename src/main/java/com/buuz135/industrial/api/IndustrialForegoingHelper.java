package com.buuz135.industrial.api;


import com.buuz135.industrial.api.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public class IndustrialForegoingHelper {

    public static final String MOD_ID = "industrialforegoing";
    public static final String API_VERSION = "4";
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
            BioReactorEntry.BIO_REACTOR_ENTRIES.removeIf(entry -> entry.doesStackMatch(stack));
            return true;
        }
        return false;
    }

    /**
     * Adds a new LaserEntry entry that wasn't already there.
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

    /**
     * Removes a LaserDrill entry.
     *
     * @param stack The ItemStack of a LaserDrill entry to remove.
     * @return true if it is removed, false if don't.
     */
    public static boolean removeLaserDrillEntry(ItemStack stack) {
        if (LaserDrillEntry.LASER_DRILL_ENTRIES.stream().anyMatch(entry -> stack.isItemEqual(entry.getStack()))) {
            LaserDrillEntry.LASER_DRILL_ENTRIES.removeIf(entry -> entry.getStack().isItemEqual(stack));
        }
        return false;
    }

    /**
     * Adds a new SludgeRefiner entry that wasn't already there.
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

    /**
     * Removes a SludgeRefiner entry.
     *
     * @param stack The ItemStack of a SludgeRefiner entry to remove.
     * @return true if it is removed, false if don't.
     */
    public static boolean removeSludgeRefinerEntry(ItemStack stack) {
        if (SludgeEntry.SLUDGE_RECIPES.stream().anyMatch(entry -> entry.getStack().isItemEqual(stack))) {
            SludgeEntry.SLUDGE_RECIPES.removeIf(entry -> entry.getStack().isItemEqual(stack));
            return true;
        }
        return false;
    }

    /**
     * Adds a new ProteinReactor entry that wasn't already there.
     *
     * @param entry The ProteinReactor entry to add.
     * @return true if it is added, false if don't.
     */
    public static boolean addProteinReactorEntry(ProteinReactorEntry entry) {
        if (ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.stream().noneMatch(entry1 -> entry.doesStackMatch(entry1.getStack()))) {
            ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.add(entry);
            return true;
        }
        return false;
    }

    /**
     * Removes a ProteinReactor entry.
     *
     * @param stack The ItemStack of a ProteinReactor entry to remove.
     * @return true if it is removed, false if don't.
     */
    public static boolean removeProteinReactorEntry(ItemStack stack) {
        if (ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.stream().anyMatch(entry -> entry.doesStackMatch(stack))) {
            ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.removeIf(entry -> entry.doesStackMatch(stack));
            return true;
        }
        return false;
    }

    /**
     * Adds a FluidDictionaryEntry that wasn't already there
     *
     * @param entry The FluidDictionaryEntry to add.
     * @return true if it's added, false if don't.
     */
    public static boolean addFluidDictionaryEntry(FluidDictionaryEntry entry) {
        if (FluidRegistry.isFluidRegistered(entry.getFluidOrigin()) && FluidRegistry.isFluidRegistered(entry.getFluidResult()) && FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.stream().noneMatch(entry1 -> entry1.getFluidOrigin().equals(entry.getFluidOrigin()) && entry1.getFluidResult().equals(entry.getFluidResult()))) {
            FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.add(entry);
            return true;
        }
        return false;
    }

    /**
     * Removes a FluidDictionaryEntry if it exists
     *
     * @param entry The Entry to remove
     * @return true if it is removed, false if don't.
     */
    public static boolean removeFluidDictionaryEntry(FluidDictionaryEntry entry) {
        if (FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.stream().anyMatch(entry1 -> entry1.getFluidOrigin().equals(entry.getFluidOrigin()) && entry1.getFluidResult().equals(entry.getFluidResult()))) {
            FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.removeIf(entry1 -> entry1.getFluidOrigin().equals(entry.getFluidOrigin()) && entry1.getFluidResult().equals(entry.getFluidResult()));
            return true;
        }
        return false;
    }
}
