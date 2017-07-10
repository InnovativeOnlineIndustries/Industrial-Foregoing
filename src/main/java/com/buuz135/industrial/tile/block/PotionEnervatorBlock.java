package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.PotionEnervatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class PotionEnervatorBlock extends CustomOrientedBlock<PotionEnervatorTile> {

    public PotionEnervatorBlock() {
        super("potion_enervator", PotionEnervatorTile.class, Material.ROCK, 5000, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "gmg", "rgr",
                'p', ItemRegistry.plastic,
                'b', Items.BREWING_STAND,
                'g', "gearGold",
                'm', MachineCaseItem.INSTANCE,
                'r', Items.REPEATER);
    }
}
