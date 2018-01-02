package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.BlockPlacerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class BlockPlacerBlock extends CustomAreaOrientedBlock<BlockPlacerTile> {

    public BlockPlacerBlock() {
        super("block_placer", BlockPlacerTile.class, Material.ROCK, 100, 20, RangeType.FRONT, 0, 0, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pdp", "dmd", "prp",
                'p', ItemRegistry.plastic,
                'd', Blocks.DROPPER,
                'm', MachineCaseItem.INSTANCE,
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

}
