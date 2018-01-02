package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.BlockPlacerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class BlockPlacerBlock extends CustomAreaOrientedBlock<BlockPlacerTile> {

    public BlockPlacerBlock() {
        super("block_placer", BlockPlacerTile.class, Material.ROCK, 100, 20, RangeType.FRONT, 0, 0, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pdp", "dmd", "prp",
                'p', ItemRegistry.plastic,
                'd', Blocks.DROPPER,
                'm', MachineCaseItem.INSTANCE,
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power, it will " + PageText.bold("place") + " in front of it the blocks in its inventory."));
        return pages;
    }
}
