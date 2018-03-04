package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.PlantInteractorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class PlantInteractorBlock extends CustomAreaOrientedBlock<PlantInteractorTile> {

    public PlantInteractorBlock() {
        super("plant_interactor", PlantInteractorTile.class, Material.ROCK, 400, 40, RangeType.FRONT, 1, 4, true);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "php", "hmh", "grg",
                'p', ItemRegistry.plastic,
                'h', Items.IRON_HOE,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }
}
