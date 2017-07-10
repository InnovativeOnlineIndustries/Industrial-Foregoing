package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentRefinerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class EnchantmentRefinerBlock extends CustomOrientedBlock<EnchantmentRefinerTile> {

    public EnchantmentRefinerBlock() {
        super("enchantment_refiner", EnchantmentRefinerTile.class, Material.ROCK, 400, 10);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this, 1), "pep", "bmn", "pgp",
                'p', ItemRegistry.plastic,
                'e', Items.ENDER_PEARL,
                'b', Items.BOOK,
                'm', MachineCaseItem.INSTANCE,
                'n', Items.ENCHANTED_BOOK,
                'g', "gearDiamond");
    }

}
