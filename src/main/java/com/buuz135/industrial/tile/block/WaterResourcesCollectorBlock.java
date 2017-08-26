package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.WaterResourcesCollectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class WaterResourcesCollectorBlock extends CustomOrientedBlock<WaterResourcesCollectorTile> {

    public WaterResourcesCollectorBlock() {
        super("water_resources_collector", WaterResourcesCollectorTile.class, Material.ROCK, 5000, 80);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "bmb", "grg",
                'p', "itemRubber",
                'f', Items.FISHING_ROD,
                'b', Items.BUCKET,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
