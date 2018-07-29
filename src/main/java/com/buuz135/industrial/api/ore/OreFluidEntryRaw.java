package com.buuz135.industrial.api.ore;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class OreFluidEntryRaw {

    public static List<OreFluidEntryRaw> ORE_RAW_ENTRIES = new ArrayList<>();

    private String ore;
    private FluidStack input;
    private FluidStack output;
    private List<ItemStack> cachedOres;

    public OreFluidEntryRaw(String ore, FluidStack input, FluidStack output) {
        this.ore = ore;
        this.input = input;
        this.output = output;
        this.cachedOres = OreDictionary.getOres(ore);
    }

    public String getOre() {
        return ore;
    }

    public FluidStack getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }

    public boolean matches(ItemStack item, FluidStack fluid) {
        for (ItemStack cachedOre : cachedOres) {
            if (cachedOre.isItemEqual(item) && input.isFluidEqual(fluid)) return true;
        }
        return false;
    }
}
