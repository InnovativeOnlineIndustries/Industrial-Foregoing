package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.FrosterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class FrosterBlock extends CustomOrientedBlock<FrosterTile> {

    public FrosterBlock() {
        super("froster", FrosterTile.class, Material.ROCK, 4000, 40);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pip", "srs", "pgp",
                'p', ItemRegistry.plastic,
                'i', Blocks.ICE,
                'r', MachineCaseItem.INSTANCE,
                's', Items.SNOWBALL,
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }
}
