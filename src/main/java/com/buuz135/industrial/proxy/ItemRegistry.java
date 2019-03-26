/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
import com.buuz135.industrial.item.addon.*;
import com.buuz135.industrial.item.addon.movility.FluidTransferAddon;
import com.buuz135.industrial.item.addon.movility.ItemStackTransferAddon;
import com.buuz135.industrial.item.addon.movility.TransferAddon;
import com.buuz135.industrial.item.infinity.ItemInfinityDrill;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;


public class ItemRegistry {

    public static MeatFeederItem meatFeederItem = new MeatFeederItem();
    public static MobImprisonmentToolItem mobImprisonmentToolItem = new MobImprisonmentToolItem();
    public static IFCustomItem tinyDryRubber = new IFCustomItem("tinydryrubber");
    public static IFCustomItem dryRubber = new IFCustomItem("dryrubber");
    public static IFCustomItem plastic = new IFCustomItem("plastic");
    public static FertilizerItem fertilizer = new FertilizerItem();
    public static LaserLensItem laserLensItem = new LaserLensItem(false);
    public static LaserLensItem laserLensItem_inverted = new LaserLensItem(true);
    public static IFCustomItem pinkSlime = new IFCustomItem("pink_slime");
    public static BookManualItem bookManualItem = new BookManualItem();
    public static IFCustomItem pinkSlimeIngot = new IFCustomItem("pink_slime_ingot");
    public static ItemStraw strawItem = new ItemStraw();
    public static ItemConveyorUpgrade conveyorUpgradeItem = new ItemConveyorUpgrade();
    public static ItemArtificalDye artificalDye;
    public static ItemInfinityDrill itemInfinityDrill = new ItemInfinityDrill();

    public static AdultFilterAddonItem adultFilterAddomItem = new AdultFilterAddonItem();
    public static RangeAddonItem rangeAddonItem = new RangeAddonItem();
    public static EnergyFieldAddon energyFieldAddon = new EnergyFieldAddon();
    public static LeafShearingAddonItem leafShearingAddonItem = new LeafShearingAddonItem();
    public static ItemStackTransferAddon itemStackTransferAddonPush = new ItemStackTransferAddon(TransferAddon.ActionMode.PUSH);
    public static ItemStackTransferAddon itemStackTransferAddonPull = new ItemStackTransferAddon(TransferAddon.ActionMode.PULL);
    public static FluidTransferAddon fluidTransferAddonPush = new FluidTransferAddon(TransferAddon.ActionMode.PUSH);
    public static FluidTransferAddon fluidTransferAddonPull = new FluidTransferAddon(TransferAddon.ActionMode.PULL);
    public static FortuneAddonItem fortuneAddonItem = new FortuneAddonItem();

    public static void registerItems(IForgeRegistry<Item> itemRegistry) {
        tinyDryRubber.register(itemRegistry);
        dryRubber.register(itemRegistry);
        plastic.register(itemRegistry);
        fertilizer.register(itemRegistry);
        meatFeederItem.register(itemRegistry);
        mobImprisonmentToolItem.register(itemRegistry);
        laserLensItem.register(itemRegistry);
        laserLensItem_inverted.register(itemRegistry);
        strawItem.register(itemRegistry);
        conveyorUpgradeItem.register(itemRegistry);
        pinkSlime.register(itemRegistry);
        bookManualItem.register(itemRegistry);
        pinkSlimeIngot.register(itemRegistry);
        itemInfinityDrill.register(itemRegistry);

        adultFilterAddomItem.registerItem(itemRegistry);
        rangeAddonItem.registerItem(itemRegistry);
        energyFieldAddon.registerItem(itemRegistry);
        leafShearingAddonItem.registerItem(itemRegistry);
        itemStackTransferAddonPull.registerItem(itemRegistry);
        itemStackTransferAddonPush.registerItem(itemRegistry);
        fluidTransferAddonPull.registerItem(itemRegistry);
        fluidTransferAddonPush.registerItem(itemRegistry);
        fortuneAddonItem.registerItem(itemRegistry);

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
        GameRegistry.addSmelting(dryRubber, new ItemStack(plastic), 0);
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
        fortuneAddonItem.createRecipe();
        itemInfinityDrill.createRecipe();
        RecipeUtils.addShapelessRecipe(new ItemStack(dryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber));
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

    public static void poke() {

    }
}