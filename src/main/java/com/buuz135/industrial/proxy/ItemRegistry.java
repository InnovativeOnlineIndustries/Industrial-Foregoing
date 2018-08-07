/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
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
package com.buuz135.industrial.proxy;

import com.buuz135.industrial.item.*;
import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.item.addon.EnergyFieldAddon;
import com.buuz135.industrial.item.addon.LeafShearingAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.item.addon.movility.FluidTransferAddon;
import com.buuz135.industrial.item.addon.movility.ItemStackTransferAddon;
import com.buuz135.industrial.item.addon.movility.TransferAddon;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;


public class ItemRegistry {

    public static MeatFeederItem meatFeederItem;
    public static MobImprisonmentToolItem mobImprisonmentToolItem;
    public static IFCustomItem tinyDryRubber;
    public static IFCustomItem dryRubber;
    public static IFCustomItem plastic;
    public static FertilizerItem fertilizer;
    public static LaserLensItem laserLensItem;
    public static LaserLensItem laserLensItem_inverted;
    public static IFCustomItem pinkSlime;
    public static BookManualItem bookManualItem;
    public static IFCustomItem pinkSlimeIngot;
    public static ItemStraw strawItem;
    public static ItemConveyorUpgrade conveyorUpgradeItem;
    public static ItemArtificalDye artificalDye;

    public static AdultFilterAddonItem adultFilterAddomItem;
    public static RangeAddonItem rangeAddonItem;
    public static EnergyFieldAddon energyFieldAddon;
    public static LeafShearingAddonItem leafShearingAddonItem;
    public static ItemStackTransferAddon itemStackTransferAddonPush;
    public static ItemStackTransferAddon itemStackTransferAddonPull;
    public static FluidTransferAddon fluidTransferAddonPush;
    public static FluidTransferAddon fluidTransferAddonPull;

    public static void registerItems(IForgeRegistry<Item> itemRegistry) {
        (tinyDryRubber = new IFCustomItem("tinydryrubber")).register(itemRegistry);
        (dryRubber = new IFCustomItem("dryrubber")).register(itemRegistry);
        RecipeUtils.addShapelessRecipe(new ItemStack(dryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber));
        (plastic = new IFCustomItem("plastic")).register(itemRegistry);
        GameRegistry.addSmelting(dryRubber, new ItemStack(plastic), 0);
        (fertilizer = new FertilizerItem()).register(itemRegistry);
        (meatFeederItem = new MeatFeederItem()).register(itemRegistry);
        (mobImprisonmentToolItem = new MobImprisonmentToolItem()).register(itemRegistry);
        (laserLensItem = new LaserLensItem(false)).register(itemRegistry);
        (laserLensItem_inverted = new LaserLensItem(true)).register(itemRegistry);
        (strawItem = new ItemStraw()).register(itemRegistry);
        (conveyorUpgradeItem = new ItemConveyorUpgrade()).register(itemRegistry);
        (pinkSlime = new IFCustomItem("pink_slime")).register(itemRegistry);
        (bookManualItem = new BookManualItem()).register(itemRegistry);
        (pinkSlimeIngot = new IFCustomItem("pink_slime_ingot")).register(itemRegistry);

        (adultFilterAddomItem = new AdultFilterAddonItem()).registerItem(itemRegistry);
        (rangeAddonItem = new RangeAddonItem()).registerItem(itemRegistry);
        (energyFieldAddon = new EnergyFieldAddon()).registerItem(itemRegistry);
        (leafShearingAddonItem = new LeafShearingAddonItem()).registerItem(itemRegistry);
        (itemStackTransferAddonPull = new ItemStackTransferAddon(TransferAddon.ActionMode.PULL)).registerItem(itemRegistry);
        (itemStackTransferAddonPush = new ItemStackTransferAddon(TransferAddon.ActionMode.PUSH)).registerItem(itemRegistry);
        (fluidTransferAddonPull = new FluidTransferAddon(TransferAddon.ActionMode.PULL)).registerItem(itemRegistry);
        (fluidTransferAddonPush = new FluidTransferAddon(TransferAddon.ActionMode.PUSH)).registerItem(itemRegistry);

        OreDictionary.registerOre("itemRubber", plastic);
        OreDictionary.registerOre("slimeball", pinkSlime);
        OreDictionary.registerOre("slimeballPink", pinkSlime);
        OreDictionary.registerOre("dyeBrown", fertilizer);
        OreDictionary.registerOre("fertilizer", fertilizer);

        if (BlockRegistry.dyeMixerBlock.isEnabled()) {
            (artificalDye = new ItemArtificalDye()).register(itemRegistry);
            OreDictionary.registerOre("dye", new ItemStack(artificalDye, 1, OreDictionary.WILDCARD_VALUE));

            String[] dyes = {"White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
            for (int i = 0; i < 16; i++) {
                OreDictionary.registerOre("dye" + dyes[i], new ItemStack(artificalDye, 1, i));
            }
        }
    }

    public static void createRecipes() {
        meatFeederItem.createRecipe();
        mobImprisonmentToolItem.createRecipe();
        laserLensItem.createRecipe();
        laserLensItem_inverted.createRecipe();
        adultFilterAddomItem.createRecipe();
        rangeAddonItem.createRecipe();
        energyFieldAddon.createRecipe();
        bookManualItem.createRecipe();
        leafShearingAddonItem.createRecipe();
        fluidTransferAddonPull.createRecipe();
        fluidTransferAddonPush.createRecipe();
        itemStackTransferAddonPull.createRecipe();
        itemStackTransferAddonPush.createRecipe();
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 0), "ipi", "idi", "ici",
                'i', "ingotIron",
                'p', plastic,
                'd', Blocks.DISPENSER,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 1), "ipi", "idi", "ici",
                'i', "ingotIron",
                'p', plastic,
                'd', Blocks.HOPPER,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 2), "ipi", "idi", "ici",
                'i', "ingotIron",
                'p', Blocks.STONE_PRESSURE_PLATE,
                'd', Items.COMPARATOR,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 3), "ipi", "idi", "ici",
                'i', "ingotIron",
                'p', Blocks.SLIME_BLOCK,
                'd', Blocks.PISTON,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 4), "ipi", "idi", "ici",
                'i', "ingotIron",
                'p', Blocks.IRON_BARS,
                'd', Blocks.DROPPER,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 5), "ipi", "idi", "ici",
                'i', "ingotIron",
                'p', Items.CHORUS_FRUIT,
                'd', Blocks.PISTON,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
        RecipeUtils.addShapedRecipe(new ItemStack(conveyorUpgradeItem, 1, 6), "ici", "idi", "ici",
                'i', "ingotIron",
                'd', Blocks.HOPPER,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE));
    }
}