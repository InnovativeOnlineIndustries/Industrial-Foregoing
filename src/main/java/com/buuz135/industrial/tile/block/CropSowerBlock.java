package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.CropSowerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class CropSowerBlock extends CustomAreaOrientedBlock<CropSowerTile> {

    public CropSowerBlock() {
        super("crop_sower", CropSowerTile.class, Material.ROCK, 400, 40, RangeType.UP, 1, 0, true);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "ama", "grg",
                'p', ItemRegistry.plastic,
                'f', Items.FLOWER_POT,
                'a', Blocks.PISTON,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("Plants seeds and saplings to be gathered in the " + PageText.bold("Plant Gatherer") + ". You can " + PageText.bold("lock") + " the slots by clicking in the lock and it will " + PageText.bold("filter") + " the inputs.\n\nThe " + PageText.bold("colored") + " background represents the colored parts in the " + PageText.bold("top") + " of the block."));
        return pages;
    }
}
