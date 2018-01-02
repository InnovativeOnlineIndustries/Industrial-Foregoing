package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentExtractorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class EnchantmentExtractorBlock extends CustomOrientedBlock<EnchantmentExtractorTile> {

    public EnchantmentExtractorBlock() {
        super("enchantment_extractor", EnchantmentExtractorTile.class, Material.ROCK, 5000, 100);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pnp", "bmb", "dgd",
                'p', ItemRegistry.plastic,
                'n', Blocks.NETHER_BRICK,
                'b', Items.BOOK,
                'm', MachineCaseItem.INSTANCE,
                'd', "gemDiamond",
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

}
