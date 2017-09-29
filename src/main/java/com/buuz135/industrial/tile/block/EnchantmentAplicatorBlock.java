package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentAplicatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

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

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will act like and " + PageText.bold("anvil") + " but instead of player experience levels it will use " + PageText.bold("Essence") + " to achieve it."));
        return pages;
    }
}
