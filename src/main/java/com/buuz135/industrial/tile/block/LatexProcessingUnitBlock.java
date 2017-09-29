package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.tile.world.LatexProcessingUnitTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class LatexProcessingUnitBlock extends CustomOrientedBlock<LatexProcessingUnitTile> {

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", LatexProcessingUnitTile.class, Material.ROCK, 200, 5);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "prp", "fmf", "pgp",
                'p', "ingotIron",
                'r', Items.REDSTONE,
                'f', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron");
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provider with " + PageText.bold("power") + ", " + PageText.bold("water") + " and " + PageText.bold("latex") + " will produce tiny dry rubber.\n\nIt will consume " + PageText.bold("75") + "mb of latex and " + PageText.bold("1000") + "mb of water to produce " + PageText.bold("1") + " tiny dry rubber."));
        return pages;
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GETTING_STARTED;
    }
}
