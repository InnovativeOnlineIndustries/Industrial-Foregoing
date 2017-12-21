package com.buuz135.industrial.tile.block;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IHasAdvancedTooltip {

    public List<String> getTooltip(ItemStack stack);
}
