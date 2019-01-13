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
import com.buuz135.industrial.tile.generator.PetrifiedFuelGeneratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class PetrifiedFuelGeneratorBlock extends CustomOrientedBlock<PetrifiedFuelGeneratorTile> {

    @Getter
    private int timeModifier;
    @Getter
    private float burnTimeMultiplier;

    public PetrifiedFuelGeneratorBlock() {
        super("petrified_fuel_generator", PetrifiedFuelGeneratorTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this, 1), "pdp", "gmg", "pfp",
                'p', ItemRegistry.plastic,
                'd', "gemDiamond",
                'g', "gearGold",
                'm', MachineCaseItem.INSTANCE,
                'f', Blocks.FURNACE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        timeModifier = CustomConfiguration.config.getInt("timeModifier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 20, 1, Integer.MAX_VALUE, "This config changes the RF/t the fuel produces based in the fuel burning time also changing for how much it burns. (RF/t = FuelBurningTime/%value%)");
        burnTimeMultiplier = CustomConfiguration.config.getFloat("burnTimeMultiplier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 0.75f, 0, Float.MAX_VALUE, "This config only changes for how much time a fuel burns. (FuelBurningTime = ItemBurningTime*%value%)");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GENERATORS;
    }

}
