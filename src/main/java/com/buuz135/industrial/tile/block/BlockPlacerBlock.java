package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.BlockPlacerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class BlockPlacerBlock extends CustomOrientedBlock<BlockPlacerTile> {

    public BlockPlacerBlock() {
        super("block_placer", BlockPlacerTile.class, Material.ROCK, 100, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pdp", "dmd", "prp",
                'p', "itemRubber",
                'd', Blocks.DROPPER,
                'm', MachineCaseItem.INSTANCE,
                'r', Items.REDSTONE);
    }
}
