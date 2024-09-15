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

package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.tile.PitifulGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nonnull;

public class PitifulGeneratorBlock extends IndustrialBlock<PitifulGeneratorTile> {

    public PitifulGeneratorBlock() {
        super("pitiful_generator", Properties.ofFullCopy(Blocks.COBBLESTONE), PitifulGeneratorTile.class, ModuleGenerator.TAB_GENERATOR);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<PitifulGeneratorTile> getTileEntityFactory() {
        return PitifulGeneratorTile::new;
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("pdp").pattern("gmg").pattern("pfp")
                .define('p', Blocks.COBBLESTONE)
                .define('d', Tags.Items.INGOTS_GOLD)
                .define('g', Blocks.IRON_BARS)
                .define('m', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .define('f', Blocks.FURNACE)
                .save(consumer);
    }
}
