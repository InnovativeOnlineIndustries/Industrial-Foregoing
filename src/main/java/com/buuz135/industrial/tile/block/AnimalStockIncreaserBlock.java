package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.AnimalStockIncreaserTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class AnimalStockIncreaserBlock extends CustomOrientedBlock<AnimalStockIncreaserTile> {

    public AnimalStockIncreaserBlock() {
        super("animal_stock_increaser", AnimalStockIncreaserTile.class, Material.ROCK, 400, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "cmc", "dad",
                'p', ItemRegistry.plastic,
                'g', Items.GOLDEN_APPLE,
                'c', Items.GOLDEN_CARROT,
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'a', "gearIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.ANIMAL_HUSBANDRY;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provider with power and " + PageText.bold("food") + " for the animals it will feed them and put them in the mood. It needs the breeding item for the animal.\n\nThere can only be " + PageText.bold("20") + " animals at the same time in the working area."));
        return pages;
    }
}
