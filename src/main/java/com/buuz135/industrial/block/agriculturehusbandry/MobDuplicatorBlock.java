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
