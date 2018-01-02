package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageItemList;
import com.buuz135.industrial.api.recipe.SludgeEntry;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.SludgeRefinerTile;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SludgeRefinerBlock extends CustomOrientedBlock<SludgeRefinerTile> {

    private static ArrayList<ItemStackWeightedItem> OUTPUTS;

    public SludgeRefinerBlock() {
        super("sludge_refiner", SludgeRefinerTile.class, Material.ROCK, 200, 10);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
    }

    public List<ItemStackWeightedItem> getItems() {
        if (OUTPUTS == null) {
            OUTPUTS = new ArrayList<>();
            SludgeEntry.SLUDGE_RECIPES.forEach(entry -> OUTPUTS.add(new ItemStackWeightedItem(entry.getStack(), entry.getWeight())));
        }
        return OUTPUTS;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "fmf", "igi",
                'p', ItemRegistry.plastic,
                'b', Items.BUCKET,
                'f', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'i', "gearIron",
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.addAll(PageItemList.generatePagesFromItemStacks(SludgeEntry.SLUDGE_RECIPES.stream().map(SludgeEntry::getStack).collect(Collectors.toList()), I18n.format("text.book.produced_items")));
        return pages;
    }
}
