package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.ItemSplitterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

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

}
