/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
