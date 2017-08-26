package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.SporesRecreatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class SporesRecreatorBlock extends CustomOrientedBlock<SporesRecreatorTile> {

    public SporesRecreatorBlock() {
        super("spores_recreator", SporesRecreatorTile.class, Material.ROCK, 400, 10);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "omo", "pgp",
                'p', "itemRubber",
                'o', Blocks.RED_MUSHROOM,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron");
    }
}
