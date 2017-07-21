package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.AnimalGrowthIncreaserTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class AnimalGrowthIncreaserBlock extends CustomOrientedBlock<AnimalGrowthIncreaserTile> {

    public AnimalGrowthIncreaserBlock() {
        super("animal_growth_increaser", AnimalGrowthIncreaserTile.class, Material.ROCK, 20 * 20, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "eme", "dgd",
                'p', ItemRegistry.plastic,
                'e', "wheat",
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'g', "gearGold");
    }
}
