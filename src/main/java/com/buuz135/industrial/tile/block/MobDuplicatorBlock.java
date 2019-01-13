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
import com.buuz135.industrial.tile.mob.MobDuplicatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.Arrays;
import java.util.List;

public class MobDuplicatorBlock extends CustomAreaOrientedBlock<MobDuplicatorTile> {

    public int essenceNeeded;
    public List<String> blacklistedEntities;
    public boolean enableExactCopy;

    public MobDuplicatorBlock() {
        super("mob_duplicator", MobDuplicatorTile.class, Material.ROCK, 5000, 80, RangeType.UP, 5, 2, true);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "cmc", "ere",
                'p', ItemRegistry.plastic,
                'w', Items.NETHER_WART,
                'c', Items.MAGMA_CREAM,
                'm', MachineCaseItem.INSTANCE,
                'e', "gemEmerald",
                'r', Items.REDSTONE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceNeeded = CustomConfiguration.config.getInt("essenceNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 12, 1, Integer.MAX_VALUE, "Essence needed based on mob's health (mobHealth*essenceNeeded)");
        blacklistedEntities = Arrays.asList(CustomConfiguration.config.getStringList("blacklistedEntities", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), new String[]{}, "A list of blacklisted entities like minecraft:creeper"));
        enableExactCopy = CustomConfiguration.config.getBoolean("enableExactCopy", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), false, "Set to true to enable exact copy in the Mob Duplicator.");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

}
