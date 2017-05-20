package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.TreeFluidExtractorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TreeFluidExtractorBlock extends CustomOrientedBlock<TreeFluidExtractorTile> {

    public TreeFluidExtractorBlock() {
        super("tree_fluid_extractor", TreeFluidExtractorTile.class, Material.ROCK, 0, 0);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "srs", "sfs", "sgs",
                's', Blocks.STONE,
                'r', Items.REDSTONE,
                'f', Blocks.FURNACE,
                'g', "gearIron");
    }
}
