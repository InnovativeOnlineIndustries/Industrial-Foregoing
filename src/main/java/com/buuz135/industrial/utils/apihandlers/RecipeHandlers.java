package com.buuz135.industrial.utils.apihandlers;

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
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


    public static NonNullList<ItemStack> getRealOredictedItems(String oredit) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (ItemStack ore : OreDictionary.getOres(oredit)) {
            if (ore.getItem().getCreativeTab() != null)
                ore.getItem().getSubItems(ore.getItem().getCreativeTab(), stacks);
        }
        return stacks;
    }
}


