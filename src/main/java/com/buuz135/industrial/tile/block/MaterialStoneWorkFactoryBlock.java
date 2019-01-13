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
import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class MaterialStoneWorkFactoryBlock extends CustomOrientedBlock<MaterialStoneWorkFactoryTile> {

    private boolean produceExNihiloDust;
    private boolean produceSilicon;

    public MaterialStoneWorkFactoryBlock() {
        super("material_stonework_factory", MaterialStoneWorkFactoryTile.class, Material.ROCK, 400, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pip", "amf", "lrw",
                'p', ItemRegistry.plastic,
                'i', Blocks.CRAFTING_TABLE,
                'a', Items.IRON_PICKAXE,
                'f', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'l', Items.LAVA_BUCKET,
                'w', Items.WATER_BUCKET,
                'r', ItemRegistry.pinkSlime);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        produceExNihiloDust = CustomConfiguration.config.getBoolean("produceExNihiloDust", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), true, "If true, it will produce exnihilo dust crushing sand.");
        produceSilicon = CustomConfiguration.config.getBoolean("produceSilicon", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), true, "If true, it will produce AE/RS silicon crushing sand or dust if it exist and 'produceExNihiloDust' is enabled.");
    }

    public boolean produceExNihiloDust() {
        return produceExNihiloDust;
    }

    public boolean produceSilicon() {
        return produceSilicon;
    }
}
