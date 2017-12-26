package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.WaterResourcesCollectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class WaterResourcesCollectorBlock extends CustomAreaOrientedBlock<WaterResourcesCollectorTile> {

    public WaterResourcesCollectorBlock() {
        super("water_resources_collector", WaterResourcesCollectorTile.class, Material.ROCK, 5000, 80, RangeType.UP, 1, 0, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "bmb", "grg",
                'p', ItemRegistry.plastic,
                'f', Items.FISHING_ROD,
                'b', Items.BUCKET,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.ANIMAL_HUSBANDRY;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When placed over a pool of " + PageText.bold("3x3") + " pool of water it will automatically " + PageText.bold("fish") + "."));
        return pages;
    }
}
