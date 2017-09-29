package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.tile.world.TreeFluidExtractorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.List;

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
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When placed in front of any " + PageText.bold("log") + " it will produce " + PageText.bold("Liquid Latex") + ".\n\nIt will slowly " + PageText.bold("consume") + " the log, each Tree Fluid Extractor contributes to the consuming of the log."));
        return pages;
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GETTING_STARTED;
    }
}
