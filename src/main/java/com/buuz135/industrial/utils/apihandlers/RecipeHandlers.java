package com.buuz135.industrial.utils.apihandlers;

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.FluidDictionaryEntry;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import com.buuz135.industrial.api.recipe.SludgeEntry;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.utils.apihandlers.crafttweaker.CTAction;
import com.google.common.collect.LinkedListMultimap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeHandlers {

	public static final LinkedListMultimap<CTAction, BioReactorEntry> BIOREACTOR_ENTRIES = LinkedListMultimap.create();
	public static final LinkedListMultimap<CTAction, LaserDrillEntry> LASER_ENTRIES = LinkedListMultimap.create();
	public static final LinkedListMultimap<CTAction, SludgeEntry> SLUDGE_ENTRIES = LinkedListMultimap.create();
	public static final LinkedListMultimap<CTAction, ProteinReactorEntry> PROTEIN_REACTOR_ENTRIES = LinkedListMultimap.create();
	public static final LinkedListMultimap<CTAction, FluidDictionaryEntry> FLUID_DICTIONARY_ENTRIES = LinkedListMultimap.create();

	public static void loadBioReactorEntries() {
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.WHEAT_SEEDS)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.PUMPKIN_SEEDS)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.MELON_SEEDS)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.BEETROOT_SEEDS)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.CARROT)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.POTATO)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Items.NETHER_WART)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Blocks.BROWN_MUSHROOM)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Blocks.RED_MUSHROOM)));
		IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(new ItemStack(Blocks.CHORUS_FLOWER)));
		getRealOredictedItems("dye").forEach(stack -> IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(stack)));
		getRealOredictedItems("treeSapling").stream().filter(stack -> !stack.getItem().getRegistryName().getResourcePath().equals("forestry")).forEach(stack -> IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(stack)));
	}

	public static void executeCraftweakerActions() {
		BIOREACTOR_ENTRIES.forEach((ctAction, entry) -> {
			if (ctAction == CTAction.ADD) IndustrialForegoingHelper.addBioReactorEntry(entry);
			else IndustrialForegoingHelper.removeBioReactorEntry(entry.getStack());
		});
		LASER_ENTRIES.forEach((ctAction, entry) -> {
			if (ctAction == CTAction.ADD) IndustrialForegoingHelper.addLaserDrillEntry(entry);
			else IndustrialForegoingHelper.removeLaserDrillEntry(entry.getStack());
		});
		SLUDGE_ENTRIES.forEach((ctAction, entry) -> {
			if (ctAction == CTAction.ADD) IndustrialForegoingHelper.addSludgeRefinerEntry(entry);
			else IndustrialForegoingHelper.removeSludgeRefinerEntry(entry.getStack());
		});
		PROTEIN_REACTOR_ENTRIES.forEach((ctAction, entry) -> {
			if (ctAction == CTAction.ADD) IndustrialForegoingHelper.addProteinReactorEntry(entry);
			else IndustrialForegoingHelper.removeProteinReactorEntry(entry.getStack());
		});
		FLUID_DICTIONARY_ENTRIES.forEach((ctAction, entry) -> {
			if (ctAction == CTAction.ADD) IndustrialForegoingHelper.addFluidDictionaryEntry(entry);
			else IndustrialForegoingHelper.removeFluidDictionaryEntry(entry);
		});
	}

	public static void loadLaserLensEntries() {
		// Standard Lens (MC ores add up to 5000)
		checkAndAddLaserDrill(0, "oreQuartz", 400);
		checkAndAddLaserDrill(1, "oreCopper", 1000);		// IndustrialCraft2, Forestry, Thermal Foundation, Project Red
		checkAndAddLaserDrill(3, "oreDiamond", 200);
		checkAndAddLaserDrill(4, "oreGold", 500);
		checkAndAddLaserDrill(5, "oreUranium", 150);	   	// IndustrialCraft2
		checkAndAddLaserDrill(6, "oreAluminum", 400);		// Tinker's Construct, Thermal Foundation, Advanced Rocketry
		checkAndAddLaserDrill(7, "oreTin", 600);			// IndustrialCraft2, Forestry, Thermal Foundation, Project Red
		checkAndAddLaserDrill(8, "oreSilver", 400);			// Thermal Foundation, Project Red, Tech Reborn
		checkAndAddLaserDrill(10, "oreLead", 400);			// IndustrialCraft2, Thermal Foundation, Tech Reborn
		checkAndAddLaserDrill(11, "oreLapis", 700);
		checkAndAddLaserDrill(12, "oreIron", 1000);
		checkAndAddLaserDrill(13, "oreEmerald", 100);
		checkAndAddLaserDrill(14, "oreRedstone", 600);
		checkAndAddLaserDrill(15, "oreCoal", 1500);
		// Soot-Coated Lens
		checkAndAddLaserDrill(16, "oreCertusQuartz", 200);  // Applied Energistics 2
		checkAndAddLaserDrill(17, "oreTopaz", 100);         // Biomes O'Plenty
		checkAndAddLaserDrill(20, "oreYellorite", 150);		// Extreme Reactors
		checkAndAddLaserDrill(21, "orePeridot", 100);		// Project Red, Biomes O'Plenty, Tech Reborn
		checkAndAddLaserDrill(22, "oreRutile", 400);        // Advanced Rocketry
		checkAndAddLaserDrill(25, "oreApatite", 600); 	    // Forestry
		checkAndAddLaserDrill(26, "oreTanzanite", 100);     // Biomes O'Plenty
		checkAndAddLaserDrill(27, "oreSapphire", 100);		// Project Red, Biomes O'Plenty, Tech Reborn
		checkAndAddLaserDrill(28, "oreBauxite", 400);		// Tech Reborn
		checkAndAddLaserDrill(29, "oreMalachite", 100);     // Biomes O'Plenty
		checkAndAddLaserDrill(30, "oreRuby", 100);			// Project Red, Biomes O'Plenty, Tech Reborn
		checkAndAddLaserDrill(31, "oreClathrateOilShale", 200);    // Thermal Foundation
		// Gold-Coated Lens
		checkAndAddLaserDrill(33, "oreAmber", 100);         // Thaumcraft, Biomes O'Plenty
		checkAndAddLaserDrill(34, "oreDilithium", 50);      // Advanced Rocketry
		checkAndAddLaserDrill(35, "orePlatinum", 100);  	// Thermal Foundation
		checkAndAddLaserDrill(36, "orePyrite", 500);		// Tech Reborn
		checkAndAddLaserDrill(40, "oreOsmium", 200);		// Mekanism
		checkAndAddLaserDrill(41, "oreElectrotine", 400);	// Project Red
		checkAndAddLaserDrill(42, "oreGalena", 600);		// Tech Reborn
		checkAndAddLaserDrill(44, "oreNickel", 200);		// Thermal Foundation
		checkAndAddLaserDrill(46, "oreCinnabar", 300);		// Thaumcraft, Tech Reborn
		checkAndAddLaserDrill(47, "oreClathrateOilSand", 200);     // Thermal Foundation
		// Diamond-Coated Lens
		checkAndAddLaserDrill(48, "oreIridium", 50);		// IndustrialCraft2, Tech Reborn
		checkAndAddLaserDrill(49, "oreSphalerite", 500);	// Tech Reborn
		checkAndAddLaserDrill(50, "oreAmethyst", 50);       // Biomes O'Plenty
		checkAndAddLaserDrill(51, "oreSodalite", 400);	    // Tech Reborn
		checkAndAddLaserDrill(52, "oreClathrateGlowstone", 100);   // Thermal Foundation
		checkAndAddLaserDrill(56, "oreSheldonite", 100);	// Tech Reborn
		checkAndAddLaserDrill(58, "oreDraconium", 100);		// Draconic Evolution
		checkAndAddLaserDrill(59, "oreCobalt", 100);		// Tinker's Construct
		checkAndAddLaserDrill(60, "oreArdite", 100);		// Tinker's Construct
		checkAndAddLaserDrill(61, "oreClathrateEnder", 50); // Thermal Foundation
		checkAndAddLaserDrill(62, "oreClathrateRedstone", 200);    // Thermal Foundation
		checkAndAddLaserDrill(63, "oreTungsten", 200);		// Tech Reborn
	}

	public static void loadSludgeRefinerEntries() {
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Items.CLAY_BALL), 4));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.CLAY), 1));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.DIRT), 4));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.GRAVEL), 4));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.MYCELIUM), 1));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.DIRT, 1, 2), 1));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.SAND), 4));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.SAND, 1, 1), 4));
		IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(new ItemStack(Blocks.SOUL_SAND), 4));
	}

	public static void loadProteinReactorEntries() {
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.PORKCHOP)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.BEEF)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.CHICKEN)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.RABBIT)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.MUTTON)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.RABBIT_FOOT)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.ROTTEN_FLESH)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.EGG)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.SPIDER_EYE)));
		IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(Items.PORKCHOP)));
		NonNullList<ItemStack> stacks = NonNullList.create();
		getSubItems(stacks, new ItemStack(Items.FISH));
		getSubItems(stacks, new ItemStack(Items.SKULL));
		stacks.forEach(stack -> IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(stack)));
	}

	public static void loadFluidDictionaryEntries() {
		addFluidEntryDoubleDirectional("essence", "xpjuice", 1);
		addFluidEntryDoubleDirectional("essence", "experience", 1);
		addFluidEntryDoubleDirectional("xpjuice", "experience", 1);
	}

	public static void addFluidEntryDoubleDirectional(String fluidInput, String fluidOutput, double ratio) {
		IndustrialForegoingHelper.addFluidDictionaryEntry(new FluidDictionaryEntry(fluidInput, fluidOutput, ratio));
		IndustrialForegoingHelper.addFluidDictionaryEntry(new FluidDictionaryEntry(fluidOutput, fluidInput, 1 / ratio));
	}

	public static void checkAndAddLaserDrill(int meta, String oreDict, int weight) {
		NonNullList<ItemStack> stacks = getRealOredictedItems(oreDict);
		if (!CustomConfiguration.enableAdditionalLens)
			meta = meta % 16;
		if (stacks.size() > 0)
			IndustrialForegoingHelper.addLaserDrillEntry(new LaserDrillEntry(meta, stacks.get(0), weight));
	}

	public static NonNullList<ItemStack> getRealOredictedItems(String oredit) {
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (ItemStack ore : OreDictionary.getOres(oredit)) {
			if (ore.getMetadata() == OreDictionary.WILDCARD_VALUE && ore.getItem().getCreativeTab() != null)
				ore.getItem().getSubItems(ore.getItem().getCreativeTab(), stacks);
			else {
				stacks.add(ore);
				break;
			}
		}
		return stacks;
	}

	public static void getSubItems(NonNullList<ItemStack> list, ItemStack stack) {
		if (stack.getItem().getCreativeTab() != null)
			stack.getItem().getSubItems(stack.getItem().getCreativeTab(), list);

	}

}


