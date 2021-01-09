/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.InfinityChargerTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class InfinityChargerBlock extends IndustrialBlock<InfinityChargerTile> {

    public InfinityChargerBlock() {
        super("infinity_charger", Properties.from(Blocks.IRON_BLOCK), InfinityChargerTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<InfinityChargerTile> getTileEntityFactory() {
        return InfinityChargerTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("ppp").patternLine("rcr").patternLine("omo")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('r', Items.REPEATER)
                .key('c', IndustrialTags.Items.GEAR_DIAMOND)
                .key('o', Items.REDSTONE_BLOCK)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .build(consumer);
    }

}
