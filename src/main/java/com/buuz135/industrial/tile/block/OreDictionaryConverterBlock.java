package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.OreDictionaryConverterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class OreDictionaryConverterBlock extends CustomOrientedBlock<OreDictionaryConverterTile> {

    public OreDictionaryConverterBlock() {
        super("oredictionary_converter", OreDictionaryConverterTile.class);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "prp", "nib",
                'p', ItemRegistry.plastic,
                'r', Blocks.REDSTONE_BLOCK,
                'n', "nuggetIron",
                'i', "ingotIron",
                'b', "blockIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

}
