package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.VillagerTradeExchangerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class VillagerTradeExchangerBlock extends CustomOrientedBlock<VillagerTradeExchangerTile> {

    public VillagerTradeExchangerBlock() {
        super("villager_trade_exchanger", VillagerTradeExchangerTile.class, Material.ROCK, 10000, 10);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this, 1), "pbp", "rmr", "pgp",
                'p', ItemRegistry.plastic,
                'b', Items.GOLD_INGOT,
                'r', Items.EMERALD,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

}
