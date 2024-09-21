package com.buuz135.industrial.plugin.jei;

import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record StoneWorkWrapper(ItemStack input, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes,
                               ItemStack output) {

    public StoneWorkWrapper(ItemStack input, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes, ItemStack output) {
        this.input = input;
        this.modes = modes;
        this.output = output;
        while (this.modes.size() < 4) {
            this.modes.add(MaterialStoneWorkFactoryTile.ACTION_RECIPES[4]);
        }
    }
}
