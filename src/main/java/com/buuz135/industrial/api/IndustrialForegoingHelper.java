package com.buuz135.industrial.api;


import com.buuz135.industrial.api.recipe.BioReactorEntry;
import net.minecraft.item.ItemStack;

import java.util.stream.Collectors;

public class IndustrialForegoingHelper {

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
}
