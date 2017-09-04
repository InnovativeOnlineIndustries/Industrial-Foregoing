package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.BiofuelGeneratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class BiofuelGeneratorBlock extends CustomOrientedBlock<BiofuelGeneratorTile> {

    public BiofuelGeneratorBlock() {
        super("biofuel_generator", BiofuelGeneratorTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "imi", "rir",
                'p', ItemRegistry.plastic,
                'f', Blocks.FURNACE,
                'i', Blocks.PISTON,
                'm', MachineCaseItem.INSTANCE,
                'r', Items.BLAZE_ROD);
    }
}
