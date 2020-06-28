package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.EnchantmentSorterTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

public class EnchantmentSorterBlock extends IndustrialBlock<EnchantmentSorterTile> {

    public EnchantmentSorterBlock() {
        super("enchantment_sorter", Properties.from(Blocks.IRON_BLOCK), EnchantmentSorterTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<EnchantmentSorterTile> getTileEntityFactory() {
        return EnchantmentSorterTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
