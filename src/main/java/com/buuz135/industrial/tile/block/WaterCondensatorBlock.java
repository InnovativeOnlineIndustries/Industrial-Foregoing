package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.WaterCondesatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class WaterCondensatorBlock extends CustomOrientedBlock<WaterCondesatorTile> {

    public WaterCondensatorBlock() {
        super("water_condensator", WaterCondesatorTile.class, Material.ROCK, 0, 0);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pwp", "fmf", "grg",
                'p', ItemRegistry.plastic,
                'w', Items.WATER_BUCKET,
                'f', Blocks.PISTON,
                'm', TeslaCoreLib.machineCase,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
