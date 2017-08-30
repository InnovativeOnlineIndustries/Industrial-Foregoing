package com.buuz135.industrial.utils.apihandlers;

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeHandlers {

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

    public static void loadLaserLensEntries() {
        checkAndAddLaserDrill(4, "oreGold", 6);
        checkAndAddLaserDrill(12, "oreIron", 10);
        checkAndAddLaserDrill(15, "oreCoal", 12);
        checkAndAddLaserDrill(11, "oreLapis", 8);
        checkAndAddLaserDrill(3, "oreDiamond", 4);
        checkAndAddLaserDrill(14, "oreRedstone", 6);
        checkAndAddLaserDrill(0, "oreQuartz", 4);
        checkAndAddLaserDrill(5, "oreEmerald", 2);
        checkAndAddLaserDrill(13, "oreUranium", 3);
        checkAndAddLaserDrill(4, "oreSulfur", 8);
        checkAndAddLaserDrill(10, "oreGalena", 6);
        checkAndAddLaserDrill(0, "oreIridium", 2);
        checkAndAddLaserDrill(14, "oreRuby", 7);
        checkAndAddLaserDrill(11, "oreSapphire", 7);
        checkAndAddLaserDrill(12, "oreBauxite", 5);
        checkAndAddLaserDrill(12, "orePyrite", 5);
        checkAndAddLaserDrill(14, "oreCinnabar", 8);
        checkAndAddLaserDrill(15, "oreTungsten", 3);
        checkAndAddLaserDrill(0, "oreSheldonite", 1);
        checkAndAddLaserDrill(3, "orePlatinum", 2);
        checkAndAddLaserDrill(13, "orePeridot", 7);
        checkAndAddLaserDrill(11, "oreSoladite", 4);
        checkAndAddLaserDrill(14, "oreTetrahedrite", 4);
        checkAndAddLaserDrill(8, "oreTin", 8);
        checkAndAddLaserDrill(10, "oreLead", 5);
        checkAndAddLaserDrill(7, "oreSilver", 5);
        checkAndAddLaserDrill(1, "oreCopper", 10);
    }

    public static void checkAndAddLaserDrill(int meta, String oreDict, int weight) {
        NonNullList<ItemStack> stacks = getRealOredictedItems(oreDict);
        if (stacks.size() > 0)
            IndustrialForegoingHelper.addLaserDrillEntry(new LaserDrillEntry(meta, stacks.get(0), weight));
    }

    public static NonNullList<ItemStack> getRealOredictedItems(String oredit) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (ItemStack ore : OreDictionary.getOres(oredit)) {
            if (ore.getItem().getCreativeTab() != null)
                ore.getItem().getSubItems(ore.getItem().getCreativeTab(), stacks);
        }
        return stacks;
    }
}


