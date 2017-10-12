package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.ItemSplitterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class ItemSplitterBlock extends CustomOrientedBlock<ItemSplitterTile> {

    public ItemSplitterBlock() {
        super("item_splitter", ItemSplitterTile.class);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pcp", "hmh", "pgp",
                'p', ItemRegistry.plastic,
                'c', "chestWood",
                'h', Blocks.HOPPER,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("It will " + PageText.bold("split") + " the defined stack size on each slot of the allowed sides of the " + PageText.bold("output") + "."));
        return pages;
    }
}
