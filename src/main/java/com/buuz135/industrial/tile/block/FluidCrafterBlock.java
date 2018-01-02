package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.FluidCrafterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class FluidCrafterBlock extends CustomOrientedBlock<FluidCrafterTile> {

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
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }
}
