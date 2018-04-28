package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.AnimalGrowthIncreaserTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class AnimalGrowthIncreaserBlock extends CustomAreaOrientedBlock<AnimalGrowthIncreaserTile> {

    public AnimalGrowthIncreaserBlock() {
        super("animal_growth_increaser", AnimalGrowthIncreaserTile.class, Material.ROCK, 20 * 20, 20, RangeType.FRONT, 5, 1, true);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "eme", "dgd",
                'p', ItemRegistry.plastic,
                'e', Items.WHEAT,
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.ANIMAL_HUSBANDRY;
    }

}
