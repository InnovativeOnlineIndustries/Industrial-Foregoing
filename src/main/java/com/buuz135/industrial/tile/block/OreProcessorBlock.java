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
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.OreProcessorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class OreProcessorBlock extends CustomOrientedBlock<OreProcessorTile> {

    @Getter
    private int essenceFortune;

    public OreProcessorBlock() {
        super("ore_processor", OreProcessorTile.class, Material.ROCK, 1000, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pip", "ama", "brb",
                'p', ItemRegistry.plastic,
                'i', Blocks.PISTON,
                'a', Items.IRON_PICKAXE,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BOOK,
                'r', Items.REDSTONE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceFortune = CustomConfiguration.config.getInt("essenceFortune", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 200, 1, Integer.MAX_VALUE, "Amount of essence needed for each fortune level.");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }


}
