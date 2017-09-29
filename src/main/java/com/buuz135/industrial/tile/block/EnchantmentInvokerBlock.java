package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentInvokerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

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

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power, an " + PageText.bold("enchanteable") + " item and " + PageText.bold("Essence") + " it will enchant the item just like an " + PageText.bold("enchanting table") + " does.\n\nIt will use " + PageText.bold("3") + " buckets of Essence to do a " + PageText.bold("30") + " level enchant."));
        return pages;
    }
}
