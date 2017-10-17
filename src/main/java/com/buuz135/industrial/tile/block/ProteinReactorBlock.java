package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageItemList;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.ProteinReactorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProteinReactorBlock extends CustomOrientedBlock<ProteinReactorTile> {

    private int baseAmount;

    public ProteinReactorBlock() {
        super("protein_reactor", ProteinReactorTile.class, Material.ROCK, 2000, 10);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        baseAmount = CustomConfiguration.config.getInt("baseBiofuel", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 80, 1, 4000, "Base protein amount in mb");
    }

    public int getBaseAmount() {
        return baseAmount;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "sms", "bcb",
                'p', ItemRegistry.plastic,
                'e', Items.PORKCHOP,
                's', Items.EGG,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BRICK,
                'c', Items.RABBIT_FOOT);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GENERATORS;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = new ArrayList<>();
        pages.add(new PageText("When provided with " + PageText.bold("power") + " and " + PageText.bold("some animals drops") + " will produce " + PageText.bold("protein") + ".\n\nEach item will increase the efficiency of all the items by " + PageText.bold("10") + "mb, at maximum efficiency, the reactor will produce " + PageText.bold("1440") + "mb consuming " + PageText.bold("9") + " different items.\n\n"));
        pages.addAll(PageItemList.generatePagesFromItemStacks(ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.stream().map(IReactorEntry::getStack).collect(Collectors.toList()), "Accepted items:"));
        pages.addAll(super.getBookDescriptionPages());
        return pages;
    }
}
