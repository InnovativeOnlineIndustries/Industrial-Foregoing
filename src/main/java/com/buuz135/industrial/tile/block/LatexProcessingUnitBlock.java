package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.LatexProcessingUnitTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class LatexProcessingUnitBlock extends CustomOrientedBlock<LatexProcessingUnitTile> {

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", LatexProcessingUnitTile.class, Material.ROCK, 200, 5);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "prp", "fmf", "pgp",
                'p', "ingotIron",
                'r', Items.REDSTONE,
                'f', Blocks.FURNACE,
                'm', TeslaCoreLib.machineCase,
                'g', "gearIron");
    }
}
