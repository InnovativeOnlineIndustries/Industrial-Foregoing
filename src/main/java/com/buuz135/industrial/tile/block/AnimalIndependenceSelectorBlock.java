package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.AnimalIndependenceSelectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class AnimalIndependenceSelectorBlock extends CustomOrientedBlock<AnimalIndependenceSelectorTile> {

    public AnimalIndependenceSelectorBlock() {
        super("animal_independence_selector", AnimalIndependenceSelectorTile.class, Material.ROCK, 20 * 20, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "eme", "dgd",
                'p', "itemRubber",
                'e', "gemEmerald",
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'g', "gearGold");
    }
}
