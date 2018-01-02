package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.tile.world.TreeFluidExtractorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TreeFluidExtractorBlock extends CustomOrientedBlock<TreeFluidExtractorTile> {

    public TreeFluidExtractorBlock() {
        super("tree_fluid_extractor", TreeFluidExtractorTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "srs", "sfs", "sgs",
                's', Blocks.STONE,
                'r', Items.REDSTONE,
                'f', Blocks.FURNACE,
                'g', "gearIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GETTING_STARTED;
    }
}
