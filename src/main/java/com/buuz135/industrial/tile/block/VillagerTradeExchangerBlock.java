package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.VillagerTradeExchangerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

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

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power and a " + PageText.bold("Mob Imprisonent Tool") + " with a " + PageText.bold("Villager") + " in it, will give you access to automatically trade with the villager."));
        return pages;
    }
}
