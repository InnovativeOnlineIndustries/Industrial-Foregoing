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
package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.core.tile.LatexProcessingUnitTile;
import com.buuz135.industrial.config.MachineCoreConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@ConfigFile.Child(MachineCoreConfig.class)
public class LatexProcessingUnitBlock extends IndustrialBlock<LatexProcessingUnitTile> {

    @ConfigVal(comment = "Power consumed every tick when the machine is working")
    public static int POWER_CONSUMED_EVERY_TICK = 20;

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", Block.Properties.from(Blocks.STONE), LatexProcessingUnitTile.class, ModuleCore.TAB_CORE);
    }

    @Override
    public IFactory<LatexProcessingUnitTile> getTileEntityFactory() {
        return LatexProcessingUnitTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("IGI").patternLine("BML").patternLine("IFI")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('G', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('B', Items.WATER_BUCKET)
                .key('L', ModuleCore.LATEX.getBucketFluid())
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('F', Blocks.FURNACE)
                .build(consumer);
    }
}
