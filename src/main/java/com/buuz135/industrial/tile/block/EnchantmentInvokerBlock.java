package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.magic.EnchantmentInvokerTile;
import net.minecraft.block.material.Material;

public class EnchantmentInvokerBlock extends CustomOrientedBlock<EnchantmentInvokerTile> {

    public EnchantmentInvokerBlock() {
        super("enchantment_invoker", EnchantmentInvokerTile.class, Material.ROCK, 4000, 80);
    }
}
