package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentAplicatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class EnchantmentAplicatorBlock extends CustomOrientedBlock<EnchantmentAplicatorTile> {

    public EnchantmentAplicatorBlock() {
        super("enchantment_aplicator", EnchantmentAplicatorTile.class, Material.ROCK, 5000, 100);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "ama", "gag",
                'p', ItemRegistry.plastic,
                'a', Blocks.ANVIL,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron");
    }
}
