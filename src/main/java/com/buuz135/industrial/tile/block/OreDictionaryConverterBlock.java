package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.OreDictionaryConverterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("It will transform items with the same " + PageText.bold("oredicted") + " entry into the filtered item. Currently supported types:\n\n" + Arrays.stream(OreDictionaryConverterTile.ACCEPTED_ENTRIES).collect(StringBuilder::new, (stringBuilder, s) -> stringBuilder.append("- \"" + PageText.bold(s) + "\"\n"), StringBuilder::append)));
        return pages;
    }
}
