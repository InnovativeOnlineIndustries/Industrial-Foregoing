package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.DyeMixerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class DyeMixerBlock extends CustomOrientedBlock<DyeMixerTile> {

    public DyeMixerBlock() {
        super("dye_mixer", DyeMixerTile.class, Material.ROCK, 100, 10);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "rmb", "pop",
                'p', ItemRegistry.plastic,
                'g', "dyeGreen",
                'r', "dyeRed",
                'm', MachineCaseItem.INSTANCE,
                'b', "dyeBlue",
                'o', "gearGold");
    }
}
