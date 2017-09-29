package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.DyeMixerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class DyeMixerBlock extends CustomOrientedBlock<DyeMixerTile> {

    public DyeMixerBlock() {
        super("dye_mixer", DyeMixerTile.class, Material.ROCK, 100, 10);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "rmb", "pop",
                'p', ItemRegistry.plastic,
                'g', "dyeGreen",
                'r', "dyeRed",
                'm', MachineCaseItem.INSTANCE,
                'b', "dyeBlue",
                'o', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("It will produce " + PageText.bold("dye") + " more efficiently that normal crafting.\n\nGiving it " + PageText.bold("red") + " dye, " + PageText.bold("green") + " dye and " + PageText.bold("blue") + " dye will fill the color buffers. When given a " + PageText.bold("Laser Lens") + " will focus that color and produce the specified dye."));
        return pages;
    }
}
