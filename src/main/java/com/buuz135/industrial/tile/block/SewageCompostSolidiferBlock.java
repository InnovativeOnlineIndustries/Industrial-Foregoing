package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.SewageCompostSolidifierTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class SewageCompostSolidiferBlock extends CustomOrientedBlock<SewageCompostSolidifierTile> {

    public SewageCompostSolidiferBlock() {
        super("sewage_composter_solidifier", SewageCompostSolidifierTile.class, Material.ROCK, 1000, 10);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pfp", "imi", "brb",
                'p', ItemRegistry.plastic,
                'f', Blocks.FURNACE,
                'i', Blocks.PISTON,
                'm', TeslaCoreLib.machineCase,
                'b', Items.BRICK,
                'r', Items.REDSTONE);
    }
}
