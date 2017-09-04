package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.CropSowerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class CropSowerBlock extends CustomOrientedBlock<CropSowerTile> {

    public CropSowerBlock() {
        super("crop_sower", CropSowerTile.class, Material.ROCK, 400, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "ama", "grg",
                'p', ItemRegistry.plastic,
                'f', Items.FLOWER_POT,
                'a', Blocks.PISTON,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
