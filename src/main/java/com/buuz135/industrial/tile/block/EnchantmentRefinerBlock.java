package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.magic.EnchantmentRefinerTile;
import net.minecraft.block.material.Material;

public class EnchantmentRefinerBlock extends CustomOrientedBlock<EnchantmentRefinerTile> {

    public EnchantmentRefinerBlock() {
        super("enchantment_refiner", EnchantmentRefinerTile.class, Material.ROCK);
    }
}
