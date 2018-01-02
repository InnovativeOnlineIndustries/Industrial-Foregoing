package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.WaterResourcesCollectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class WaterResourcesCollectorBlock extends CustomAreaOrientedBlock<WaterResourcesCollectorTile> {

    public WaterResourcesCollectorBlock() {
        super("water_resources_collector", WaterResourcesCollectorTile.class, Material.ROCK, 5000, 80, RangeType.UP, 1, 0, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "bmb", "grg",
                'p', ItemRegistry.plastic,
                'f', Items.FISHING_ROD,
                'b', Items.BUCKET,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.ANIMAL_HUSBANDRY;
    }

}
