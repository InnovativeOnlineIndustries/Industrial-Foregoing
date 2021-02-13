package com.buuz135.industrial.block.agriculturehusbandry;

import java.util.function.Consumer;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.MobDuplicatorTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;


public class MobDuplicatorBlock extends IndustrialBlock<MobDuplicatorTile> {

	public MobDuplicatorBlock() {
		super("mob_duplicator", Properties.from(Blocks.IRON_BLOCK), MobDuplicatorTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
	}

	@Override
	public IFactory<MobDuplicatorTile> getTileEntityFactory() {
		return MobDuplicatorTile::new;
	}

	@Override
	public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
		TitaniumShapedRecipeBuilder.shapedRecipe(this)
				.patternLine("PWP").patternLine("CMC").patternLine("ERE")
				.key('P', IndustrialTags.Items.PLASTIC)
				.key('W', Items.NETHER_WART)
				.key('C', Items.MAGMA_CREAM)
				.key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
				.key('E', Items.EMERALD)
				.key('R', Items.REDSTONE)
				.build(consumer);
	}
}
