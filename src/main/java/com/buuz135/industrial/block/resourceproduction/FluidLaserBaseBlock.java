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
package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FluidLaserBaseTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class FluidLaserBaseBlock extends IndustrialBlock<FluidLaserBaseTile> {

    public FluidLaserBaseBlock() {
        super("fluid_laser_base", Properties.from(Blocks.IRON_BLOCK), FluidLaserBaseTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<FluidLaserBaseTile> getTileEntityFactory() {
        return FluidLaserBaseTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pfp").patternLine("bmb").patternLine("grg")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('f', Items.DIAMOND_PICKAXE)
                .key('b', Items.BUCKET)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('g', IndustrialTags.Items.GEAR_DIAMOND)
                .key('r', Items.REDSTONE)
                .build(consumer);
    }
}
