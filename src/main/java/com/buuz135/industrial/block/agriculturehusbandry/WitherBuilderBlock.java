package com.buuz135.industrial.block.agriculturehusbandry;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.WitherBuilderTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WitherBuilderBlock extends IndustrialBlock<WitherBuilderTile> {

	public WitherBuilderBlock() {
		super("wither_builder", Properties.copy(Blocks.IRON_BLOCK), WitherBuilderTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
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
	public void registerRecipe(Consumer<FinishedRecipe> consumer) {
		TitaniumShapedRecipeBuilder.shapedRecipe(this)
				.pattern("PNP").pattern("WCW").pattern("SSS")
				.define('P', IndustrialTags.Items.PLASTIC)
				.define('N', Items.NETHER_STAR)
				.define('W', Items.WITHER_SKELETON_SKULL)
				.define('C', IndustrialTags.Items.MACHINE_FRAME_SUPREME)
				.define('S', Blocks.SOUL_SAND)
				.save(consumer);
	}
}
