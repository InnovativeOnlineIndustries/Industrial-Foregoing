package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.CropEnrichMaterialInjectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class CropEnrichMaterialInjectorBlock extends CustomAreaOrientedBlock<CropEnrichMaterialInjectorTile> {

    public CropEnrichMaterialInjectorBlock() {
        super("crop_enrich_material_injector", CropEnrichMaterialInjectorTile.class, Material.ROCK, 400, 40, RangeType.FRONT, 1, 0, true);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "lml", "grg",
                'p', ItemRegistry.plastic,
                'b', Items.GLASS_BOTTLE,
                'l', Items.LEATHER,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power and fertilizer it will bonemeal crops and saplings.\n\nAccepted fertilizers: " + PageText.bold("bonemeal") + ", " + PageText.bold("Industrial Foregoing's") + " and " + PageText.bold("Forestry") + " fertilizer."));
        return pages;
    }
}
