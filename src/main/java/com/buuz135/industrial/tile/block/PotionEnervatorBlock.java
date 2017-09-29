package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.PotionEnervatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class PotionEnervatorBlock extends CustomOrientedBlock<PotionEnervatorTile> {

    public PotionEnervatorBlock() {
        super("potion_enervator", PotionEnervatorTile.class, Material.ROCK, 5000, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "gmg", "rgr",
                'p', ItemRegistry.plastic,
                'b', Items.BREWING_STAND,
                'g', "gearGold",
                'm', MachineCaseItem.INSTANCE,
                'r', Items.REPEATER);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will act like an advanced version of the " + PageText.bold("brewing stand") + ".\n\nFirst of all it will " + PageText.bold("fill") + " glass bottles with water then it will start " + PageText.bold("brewing") + " all the ingredients in the top row in " + PageText.bold("order") + ".\n\nFollow the " + PageText.bold("arrow") + " to check the current operation."));
        return pages;
    }
}
