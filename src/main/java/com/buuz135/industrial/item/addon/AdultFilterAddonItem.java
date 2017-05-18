package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.AnimalIndependenceSelectorTile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class AdultFilterAddonItem extends CustomAddon {

    public AdultFilterAddonItem() {
        super("adult_filter");
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return machine instanceof AnimalIndependenceSelectorTile;
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this),"gpg","gmg","gpg",
                'g',"nuggetGold",
                'p', ItemRegistry.plastic,
                'm', "egg");
    }
}
