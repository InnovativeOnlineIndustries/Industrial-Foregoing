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

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.MobDuplicatorTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;


import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MobDuplicatorBlock extends IndustrialBlock<MobDuplicatorTile> {

	public MobDuplicatorBlock() {
		super("mob_duplicator", Properties.copy(Blocks.IRON_BLOCK), MobDuplicatorTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
	}

	@Override
	public BlockEntityType.BlockEntitySupplier<MobDuplicatorTile> getTileEntityFactory() {
		return MobDuplicatorTile::new;
	}

	@Override
	public void registerRecipe(Consumer<FinishedRecipe> consumer) {
		TitaniumShapedRecipeBuilder.shapedRecipe(this)
				.pattern("PWP").pattern("CMC").pattern("ERE")
				.define('P', IndustrialTags.Items.PLASTIC)
				.define('W', Items.NETHER_WART)
				.define('C', Items.MAGMA_CREAM)
				.define('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
				.define('E', Items.EMERALD)
				.define('R', Items.REDSTONE)
				.save(consumer);
	}
}
