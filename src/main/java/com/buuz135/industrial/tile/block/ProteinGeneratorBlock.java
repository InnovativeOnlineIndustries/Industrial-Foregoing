package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.ProteinGeneratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ProteinGeneratorBlock extends CustomOrientedBlock<ProteinGeneratorTile> {


    public ProteinGeneratorBlock() {
        super("protein_generator", ProteinGeneratorTile.class);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "imi", "rir",
                'p', ItemRegistry.plastic,
                'f', Blocks.FURNACE,
                'i', Blocks.PISTON,
                'm', BlockRegistry.biofuelGeneratorBlock,
                'r', Items.SPIDER_EYE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GENERATORS;
    }

}
