package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class RedstoneInvertedAddon extends CustomAddon {

    public RedstoneInvertedAddon() {
        super("redstone_inverted");
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return machine instanceof WorkingAreaElectricMachine;
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "gpg", "gmg", "gpg",
                'g', "nuggetGold",
                'p', ItemRegistry.plastic,
                'm', Blocks.REDSTONE_TORCH);
    }
}
