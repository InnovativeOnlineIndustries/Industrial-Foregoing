package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.AnimalStockIncreaserTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class AnimalStockIncreaserBlock extends CustomAreaOrientedBlock<AnimalStockIncreaserTile> {

    public AnimalStockIncreaserBlock() {
        super("animal_stock_increaser", AnimalStockIncreaserTile.class, Material.ROCK, 400, 20, RangeType.FRONT, 3, 1, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "cmc", "dad",
                'p', ItemRegistry.plastic,
                'g', Items.GOLDEN_APPLE,
                'c', Items.GOLDEN_CARROT,
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'a', "gearIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.ANIMAL_HUSBANDRY;
    }

}
