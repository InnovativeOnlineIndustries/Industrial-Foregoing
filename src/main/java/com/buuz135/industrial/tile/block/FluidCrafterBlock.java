package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.FluidCrafterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class FluidCrafterBlock extends CustomOrientedBlock<FluidCrafterTile>{

    public FluidCrafterBlock() {
        super("fluid_crafter", FluidCrafterTile.class);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pcp", "bmb", "pgp",
                'p', ItemRegistry.plastic,
                'b', Items.BUCKET,
                'c', Blocks.CRAFTING_TABLE,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold");
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("The "+PageText.bold("fluid crafter")+" will craft anything that needs "+PageText.bold("one")+ " fluid type instead of using the "+PageText.bold("buckets")+" in the recipe. To make it work you need to put the items in the grid and "+PageText.bold("lock it")+". After that you can supply the items in the grid and the fluid in the tank.\n\nIf the recipe has multiple of one item you can use "+PageText.bold("Item Splitter")+" to use it easier."));
        return pages;
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }
}
