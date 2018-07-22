package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.FluidDictionaryConverterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class FluidDictionaryConverterBlock extends CustomOrientedBlock<FluidDictionaryConverterTile> {

    public FluidDictionaryConverterBlock() {
        super("fluiddictionary_converter", FluidDictionaryConverterTile.class);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "grg", "bib",
                'p', ItemRegistry.plastic,
                'r', MachineCaseItem.INSTANCE,
                'g', "blockGlass",
                'b', Items.BUCKET,
                'i', "gearIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }
}
