/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.api;


import com.buuz135.industrial.api.extractor.ExtractorEntry;
import com.buuz135.industrial.api.recipe.*;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class IndustrialForegoingHelper {

    public static final String MOD_ID = "industrialforegoing";
    public static final String API_VERSION = "5";
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
        return BioReactorEntry.BIO_REACTOR_ENTRIES.removeIf(entry -> entry.doesStackMatch(stack));
    }

    /**
     * Adds a new LaserEntry entry that wasn't already there.
     *
     * @param entry The LaserEntry entry to add.
     * @return true if it is added, false if don't.
     */
    @Deprecated
    public static boolean addLaserDrillEntry(LaserDrillEntry entry) {
        try {
            throw new UnsupportedOperationException("Deprecated API Method since v1.12.0, use the new config for the Laser Drill located in config/laser_drill_ores");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Removes a LaserDrill entry.
     *
     * @param stack The ItemStack of a LaserDrill entry to remove.
     * @return true if it is removed, false if don't.
     */
    @Deprecated
    public static boolean removeLaserDrillEntry(ItemStack stack) {
        try {
            throw new UnsupportedOperationException("Deprecated API Method since v1.12.0, use the new config for the Laser Drill located in config/laser_drill_ores");
        } catch (Exception e) {
            e.printStackTrace();
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
        return SludgeEntry.SLUDGE_RECIPES.removeIf(entry -> entry.getStack().isItemEqual(stack));
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
        return ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.removeIf(entry -> entry.doesStackMatch(stack));
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
        return FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.removeIf(entry1 -> entry1.getFluidOrigin().equals(entry.getFluidOrigin()) && entry1.getFluidResult().equals(entry.getFluidResult()));
    }

    public static boolean addWoodToLatex(ExtractorEntry entry) {
        if (ExtractorEntry.EXTRACTOR_ENTRIES.stream().noneMatch(extractorEntry -> extractorEntry.isEqual(entry.getItemStack()))) {
            ExtractorEntry.EXTRACTOR_ENTRIES.add(entry);
            return true;
        }
        return false;
    }

    public static boolean removeWoodToLatex(ItemStack stack) {
        return ExtractorEntry.EXTRACTOR_ENTRIES.removeIf(extractorEntry -> extractorEntry.isEqual(stack));
    }

    public static boolean addOreFluidEntryRaw(OreFluidEntryRaw raw) {
        if (OreFluidEntryRaw.ORE_RAW_ENTRIES.stream().noneMatch(raw1 -> raw1.getOre().equalsIgnoreCase(raw.getOre()))) {
            OreFluidEntryRaw.ORE_RAW_ENTRIES.add(raw);
            return true;
        }
        return false;
    }

    public static boolean removeOreFluidEntryRaw(String ore) {
        return OreFluidEntryRaw.ORE_RAW_ENTRIES.removeIf(raw -> raw.getOre().equalsIgnoreCase(ore));
    }

    public static boolean addOreFluidEntryFermenter(OreFluidEntryFermenter entryFermenter) {
        if (OreFluidEntryFermenter.ORE_FLUID_FERMENTER.stream().noneMatch(entryFermenter1 -> entryFermenter.getInput().isFluidEqual(entryFermenter1.getInput()))) {
            OreFluidEntryFermenter.ORE_FLUID_FERMENTER.add(entryFermenter);
            return true;
        }
        return false;
    }

    public static boolean removeOreFluidEntryFermenter(FluidStack input) {
        return OreFluidEntryFermenter.ORE_FLUID_FERMENTER.removeIf(raw -> raw.getInput().isFluidEqual(input));
    }

    public static boolean addOreFluidEntrySieve(OreFluidEntrySieve entrySieve) {
        if (OreFluidEntrySieve.ORE_FLUID_SIEVE.stream().noneMatch(entrySieve1 -> entrySieve1.getOutput().isItemEqual(entrySieve.getOutput()))) {
            OreFluidEntrySieve.ORE_FLUID_SIEVE.add(entrySieve);
            return true;
        }
        return false;
    }

    public static boolean removeOreFluidEntrySieve(ItemStack stack) {
        return OreFluidEntrySieve.ORE_FLUID_SIEVE.removeIf(entrySieve -> entrySieve.getOutput().isItemEqual(stack));
    }
}
