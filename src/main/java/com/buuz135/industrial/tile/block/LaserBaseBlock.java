package com.buuz135.industrial.tile.block;

import java.util.List;
import java.util.stream.Collectors;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageItemList;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import com.buuz135.industrial.utils.RecipeUtils;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class LaserBaseBlock extends CustomOrientedBlock<LaserBaseTile> {

	private int workNeeded;
	private int lenseChanceIncrease;
	private float lenseChanceMultiple;

	public LaserBaseBlock() {
		super("laser_base", LaserBaseTile.class, Material.ROCK, 100000, 100);
	}


	@Override
	public void getMachineConfig() {
		super.getMachineConfig();
		workNeeded = CustomConfiguration.config.getInt("workNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 100, 1, Integer.MAX_VALUE, "Amount of work needed to produce an ore");
		lenseChanceIncrease = CustomConfiguration.config.getInt("lenseChanceIncrease", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 500, 0, Integer.MAX_VALUE, "How much each lense adds to the weight of the ore (for reference, default is 50% of Iron chance)");
		lenseChanceMultiple = CustomConfiguration.config.getFloat("lenseChanceMultiple", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 1.0f, 0.01f, 100.0f, "How much each lense multiplies the weight of the ore");
	}

	public int getWorkNeeded() {
		return workNeeded;
	}

	public int getLenseChanceIncrease() {
		return lenseChanceIncrease;
	}

	public double getLenseChanceMultiple() {
		return lenseChanceMultiple;
	}

	@Override
	public void createRecipe() {
		RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "gwg", "dmd",
				'p', ItemRegistry.plastic,
				'w', Blocks.GLOWSTONE,
				'g', "gearGold",
				'd', "gearDiamond",
				'm', MachineCaseItem.INSTANCE);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity entity = worldIn.getTileEntity(pos);
		if (entity != null && entity instanceof LaserBaseTile) {
			((LaserBaseTile) entity).onBlockBroken();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public BookCategory getCategory() {
		return BookCategory.RESOURCE_PRODUCTION;
	}

	@Override
	public List<IPage> getBookDescriptionPages() {
		List<IPage> pages = super.getBookDescriptionPages();
		pages.addAll(PageItemList.generatePagesFromItemStacks(LaserDrillEntry.LASER_DRILL_ENTRIES.stream().map(LaserDrillEntry::getStack).collect(Collectors.toList()), I18n.format("text.book.produced_items")));
		return pages;
	}

}
