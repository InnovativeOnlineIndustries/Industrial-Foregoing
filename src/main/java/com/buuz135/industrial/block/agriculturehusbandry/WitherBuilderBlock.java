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

package com.buuz135.industrial.block.agriculturehusbandry;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.WitherBuilderTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

public class WitherBuilderBlock extends IndustrialBlock<WitherBuilderTile> {

	public WitherBuilderBlock() {
		super("wither_builder", Properties.from(Blocks.IRON_BLOCK), WitherBuilderTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
	}

	@Override
	public IFactory<WitherBuilderTile> getTileEntityFactory() {
		return WitherBuilderTile::new;
	}

	@Nonnull
	@Override
	public RotationType getRotationType() {
		return RotationType.FOUR_WAY;
	}

	@Override
	public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
		TitaniumShapedRecipeBuilder.shapedRecipe(this)
				.patternLine("PNP").patternLine("WCW").patternLine("SSS")
				.key('P', IndustrialTags.Items.PLASTIC)
				.key('N', Items.NETHER_STAR)
				.key('W', Items.WITHER_SKELETON_SKULL)
				.key('C', IndustrialTags.Items.MACHINE_FRAME_SUPREME)
				.key('S', Blocks.SOUL_SAND)
				.build(consumer);
	}
}
