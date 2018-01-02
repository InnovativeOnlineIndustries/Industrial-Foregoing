package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageItemList;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.BioReactorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;
import java.util.stream.Collectors;


public class BioReactorBlock extends CustomOrientedBlock<BioReactorTile> {

    private int baseAmount;

    public BioReactorBlock() {
        super("bioreactor", BioReactorTile.class, Material.ROCK, 2000, 10);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        baseAmount = CustomConfiguration.config.getInt("baseBiofuel", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 80, 1, 4000, "Base biofuel amount in mb");
    }


    public int getBaseAmount() {
        return baseAmount;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "sms", "bcb",
                'p', ItemRegistry.plastic,
                'e', Items.FERMENTED_SPIDER_EYE,
                's', Items.SLIME_BALL,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BRICK,
                'c', Items.SUGAR);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GENERATORS;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.addAll(PageItemList.generatePagesFromItemStacks(BioReactorEntry.BIO_REACTOR_ENTRIES.stream().map(IReactorEntry::getStack).collect(Collectors.toList()), I18n.format("text.book.accepted_items")));
        return pages;
    }
}
