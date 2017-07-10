package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.AnimalResourceHarvesterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class AnimalResourceHarvesterBlock extends CustomOrientedBlock<AnimalResourceHarvesterTile> {

    public AnimalResourceHarvesterBlock() {
        super("animal_resource_harvester", AnimalResourceHarvesterTile.class, Material.ROCK, 400, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "sbs", "gmg",
                'p', ItemRegistry.plastic,
                's', Items.SHEARS,
                'b', Items.BUCKET,
                'g', "gearGold",
                'm', MachineCaseItem.INSTANCE);
    }
}
