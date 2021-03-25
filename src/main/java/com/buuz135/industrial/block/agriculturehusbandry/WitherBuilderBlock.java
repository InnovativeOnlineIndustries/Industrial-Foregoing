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
