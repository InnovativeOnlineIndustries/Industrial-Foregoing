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
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.ProteinGeneratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class ProteinGeneratorBlock extends CustomOrientedBlock<ProteinGeneratorTile> {

    public int powerGeneration;

    public ProteinGeneratorBlock() {
        super("protein_generator", ProteinGeneratorTile.class);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        powerGeneration = CustomConfiguration.config.getInt("powerGeneration", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 320, 0, Integer.MAX_VALUE, "How much power the generator produces every tick");
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "imi", "rir",
                'p', ItemRegistry.plastic,
                'f', Blocks.FURNACE,
                'i', Blocks.PISTON,
                'm', BlockRegistry.biofuelGeneratorBlock,
                'r', Items.SPIDER_EYE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GENERATORS;
    }

}
