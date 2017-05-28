package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.DyeMixerTile;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class DyeMixerBlock extends CustomOrientedBlock<DyeMixerTile> {

    public DyeMixerBlock() {
        super("dye_mixer", DyeMixerTile.class, Material.ROCK, 100, 10);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pgp", "rmb", "pop",
                'p', ItemRegistry.plastic,
                'g', "dyeGreen",
                'r', "dyeRed",
                'm', TeslaCoreLib.machineCase,
                'b', "dyeBlue",
                'o', "gearGold");
    }
}
