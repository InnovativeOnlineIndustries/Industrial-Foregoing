package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.mob.AnimalStockIncreaserTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class AnimalStockIncreaserBlock extends CustomOrientedBlock<AnimalStockIncreaserTile> {

    public AnimalStockIncreaserBlock() {
        super("animal_stock_increaser", AnimalStockIncreaserTile.class, Material.ROCK, 400, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "cmc", "dad",
                'p', "itemRubber",
                'g', Items.GOLDEN_APPLE,
                'c', Items.GOLDEN_CARROT,
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'a', "gearIron");
    }
}
