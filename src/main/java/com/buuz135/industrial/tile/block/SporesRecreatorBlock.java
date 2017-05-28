package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.SporesRecreatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class SporesRecreatorBlock extends CustomOrientedBlock<SporesRecreatorTile> {

    public SporesRecreatorBlock() {
        super("spores_recreator", SporesRecreatorTile.class, Material.ROCK, 400, 10);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "ppp", "omo", "pgp",
                'p', ItemRegistry.plastic,
                'o', Blocks.RED_MUSHROOM,
                'm', TeslaCoreLib.machineCase,
                'g', "gearIron");
    }
}
