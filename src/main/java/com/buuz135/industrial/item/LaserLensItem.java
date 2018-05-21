package com.buuz135.industrial.item;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RecipeUtils;

import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class LaserLensItem extends IFCustomItem {

	@Getter
	private boolean inverted;

	public LaserLensItem(boolean inverted) {
		super("laser_lens" + (inverted ? "_inverted" : ""));
		setMaxStackSize(1);
		setHasSubtypes(true);
		this.inverted = inverted;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		int nLens = CustomConfiguration.enableAdditionalLens ? 64 : 16;
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < nLens; ++i) {
				subItems.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public void register(IForgeRegistry<Item> items) {
		super.register(items);
	}

	@Override
	public void createRecipe() {
		for (int i = 0; i < 16; ++i) {
			ItemStack currentLens = new ItemStack(this, 1, i);
			RecipeUtils.addShapedRecipe(currentLens, " i ", "ipi", " i ", 'i', inverted ? new ItemStack(Items.IRON_INGOT) : new ItemStack(ItemRegistry.pinkSlime),
					'p', new ItemStack(Blocks.STAINED_GLASS_PANE, 1, i));
			if (CustomConfiguration.enableAdditionalLens) {
				RecipeUtils.addShapelessRecipe(new ItemStack(this, 1, i + 16), currentLens, Items.COAL);
				RecipeUtils.addShapelessRecipe(new ItemStack(this, 1, i + 32), currentLens, Items.GOLD_INGOT);
				RecipeUtils.addShapelessRecipe(new ItemStack(this, 1, i + 48), currentLens, Items.DIAMOND);
			}
		}
	}

	@Override
	public void registerRender() {
		int nLens = CustomConfiguration.enableAdditionalLens ? 64 : 16;
		for (int i = 0; i < nLens; ++i) {
			ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + (i % 16), "inventory"));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String color = new TextComponentTranslation("item.fireworksCharge." + EnumDyeColor.byMetadata(stack.getMetadata() % 16).getUnlocalizedName().replaceAll("_", "")).getFormattedText() + " ";
		String coating = "";
		if (stack.getMetadata() >= 48)
			coating = "Diamond-Coated ";
		else if (stack.getMetadata() >= 32)
			coating = "Gold-Coated ";
		else if (stack.getMetadata() >= 16)
			coating = "Soot-Coated ";
		return coating + color + super.getItemStackDisplayName(stack);
	}
}
