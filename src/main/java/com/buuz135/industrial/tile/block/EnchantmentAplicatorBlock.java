package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.magic.EnchantmentAplicatorTile;
import net.minecraft.block.material.Material;

public class EnchantmentAplicatorBlock extends CustomOrientedBlock<EnchantmentAplicatorTile> {

    public EnchantmentAplicatorBlock() {
        super("enchantment_aplicator", EnchantmentAplicatorTile.class, Material.ROCK, 5000, 100);
    }
}
