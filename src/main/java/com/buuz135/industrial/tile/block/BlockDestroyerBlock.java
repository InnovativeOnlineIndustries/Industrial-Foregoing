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

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.BlockDestroyerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class BlockDestroyerBlock extends CustomAreaOrientedBlock<BlockDestroyerTile> {

    public BlockDestroyerBlock() {
        super("block_destroyer", BlockDestroyerTile.class, Material.ROCK, 100, 20, RangeType.FRONT, 0, 0, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "ams", "iri",
                'p', ItemRegistry.plastic,
                'g', "gearGold",
                'a', Items.IRON_PICKAXE,
                'm', MachineCaseItem.INSTANCE,
                's', Items.IRON_SHOVEL,
                'i', "gearIron",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> t = super.getTooltip(stack);
        t.add(new TextComponentTranslation("text.industrialforegoing.tooltip.fortune_addon").getFormattedText());
        return t;
    }

}
