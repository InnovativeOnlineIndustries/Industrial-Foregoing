package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.LavaFabricatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class LavaFabricatorBlock extends CustomOrientedBlock<LavaFabricatorTile> {
    public LavaFabricatorBlock() {
        super("lava_fabricator", LavaFabricatorTile.class, Material.ROCK, 200000, 200000);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pop", "cmc", "brb",
                'p', ItemRegistry.plastic,
                'o', Blocks.OBSIDIAN,
                'c', Items.MAGMA_CREAM,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BLAZE_ROD,
                'r', Blocks.REDSTONE_BLOCK);
    }
}
