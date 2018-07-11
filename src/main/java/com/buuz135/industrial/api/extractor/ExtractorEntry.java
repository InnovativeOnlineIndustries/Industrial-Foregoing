package com.buuz135.industrial.api.extractor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ExtractorEntry {

    public static List<ExtractorEntry> EXTRACTOR_ENTRIES = new ArrayList<>();

    private final ItemStack itemStack;
    private final FluidStack fluidStack;

    public ExtractorEntry(ItemStack itemStack, FluidStack fluidStack) {
        this.itemStack = itemStack;
        this.fluidStack = fluidStack;
    }

    public static ExtractorEntry getExtractorEntry(World world, BlockPos pos) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(world.getBlockState(pos).getBlock()), 1, world.getBlockState(pos).getBlock().damageDropped(world.getBlockState(pos)));
        if (!stack.isEmpty()) {
            for (ExtractorEntry extractorEntry : EXTRACTOR_ENTRIES) {
                if (extractorEntry.isEqual(stack)) return extractorEntry;
            }
        }
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }

    public boolean isEqual(ItemStack stack) {
        return stack.isItemEqual(this.getItemStack());
    }
}
