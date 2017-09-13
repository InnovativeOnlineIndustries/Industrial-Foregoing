package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.mob.VillagerTradeExchangerTile;
import net.minecraft.block.material.Material;

public class VillagerTradeExchangerBlock extends CustomOrientedBlock<VillagerTradeExchangerTile> {

    public VillagerTradeExchangerBlock() {
        super("villager_trade_exchanger", VillagerTradeExchangerTile.class, Material.ROCK, 10000, 10);
    }

    @Override
    public void createRecipe() {

    }
}
