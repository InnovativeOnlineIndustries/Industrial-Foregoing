package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentRefinerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

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

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power and " + PageText.bold("any") + " item it will " + PageText.bold("sort") + " enchanted items from non enchanted items.\n\nEnchanted items will go to the " + PageText.bold("top") + " row and the non enchanted ones will go to the " + PageText.bold("bottom") + " row."));
        return pages;
    }

}
