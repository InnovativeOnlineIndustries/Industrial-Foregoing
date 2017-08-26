package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.misc.WaterCondesatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class WaterCondensatorBlock extends CustomOrientedBlock<WaterCondesatorTile> {

    public WaterCondensatorBlock() {
        super("water_condensator", WaterCondesatorTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "fmf", "grg",
                'p', "itemRubber",
                'w', Items.WATER_BUCKET,
                'f', Blocks.PISTON,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
