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
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PitifulGeneratorBlock extends IndustrialBlock<PitifulGeneratorTile> {

    public PitifulGeneratorBlock() {
        super("pitiful_generator", Properties.from(Blocks.COBBLESTONE), PitifulGeneratorTile.class, ModuleGenerator.TAB_GENERATOR);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<PitifulGeneratorTile> getTileEntityFactory() {
        return PitifulGeneratorTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pdp").patternLine("gmg").patternLine("pfp")
                .key('p', Blocks.COBBLESTONE)
                .key('d', Tags.Items.INGOTS_GOLD)
                .key('g', Blocks.IRON_BARS)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('f', Blocks.FURNACE)
                .build(consumer);
    }
}
