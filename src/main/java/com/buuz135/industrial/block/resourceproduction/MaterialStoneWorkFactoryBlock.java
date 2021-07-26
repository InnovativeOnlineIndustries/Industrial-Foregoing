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
import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MaterialStoneWorkFactoryBlock extends IndustrialBlock<MaterialStoneWorkFactoryTile> {

    public MaterialStoneWorkFactoryBlock() {
        super("material_stonework_factory", Properties.copy(Blocks.IRON_BLOCK), MaterialStoneWorkFactoryTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<MaterialStoneWorkFactoryTile> getTileEntityFactory() {
        return () -> new MaterialStoneWorkFactoryTile();
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("pcp").pattern("gmf").pattern("aba")
                .define('p', IndustrialTags.Items.PLASTIC)
                .define('c', Blocks.CRAFTING_TABLE)
                .define('g', Items.DIAMOND_PICKAXE)
                .define('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .define('f', Blocks.FURNACE)
                .define('a', IndustrialTags.Items.GEAR_GOLD)
                .define('b', ModuleCore.PINK_SLIME_ITEM)
                .save(consumer);
    }
}
