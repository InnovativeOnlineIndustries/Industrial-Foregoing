package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentInvokerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class EnchantmentInvokerBlock extends CustomOrientedBlock<EnchantmentInvokerTile> {

    public EnchantmentInvokerBlock() {
        super("enchantment_invoker", EnchantmentInvokerTile.class, Material.ROCK, 4000, 80);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "dmd", "ooo",
                'p', ItemRegistry.plastic,
                'b', Items.BOOK,
                'd', "gemDiamond",
                'm', MachineCaseItem.INSTANCE,
                'o', Blocks.OBSIDIAN);
    }
}
