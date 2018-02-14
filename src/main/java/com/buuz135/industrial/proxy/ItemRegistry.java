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
    public static IFCustomItem fertilizer;
    public static LaserLensItem laserLensItem;
    public static LaserLensItem laserLensItem_inverted;
    public static IFCustomItem pinkSlime;
    public static BookManualItem bookManualItem;

    public static ItemStraw strawItem;

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
        (fertilizer = new IFCustomItem("fertilizer")).register(itemRegistry);
        (meatFeederItem = new MeatFeederItem()).register(itemRegistry);
        (mobImprisonmentToolItem = new MobImprisonmentToolItem()).register(itemRegistry);
        (laserLensItem = new LaserLensItem(false)).register(itemRegistry);
        (laserLensItem_inverted = new LaserLensItem(true)).register(itemRegistry);
        (strawItem = new ItemStraw()).register(itemRegistry);
        (pinkSlime = new IFCustomItem("pink_slime")).register(itemRegistry);
        (bookManualItem = new BookManualItem()).register(itemRegistry);

        (adultFilterAddomItem = new AdultFilterAddonItem()).registerItem(itemRegistry);
        (rangeAddonItem = new RangeAddonItem()).registerItem(itemRegistry);
        (energyFieldAddon = new EnergyFieldAddon()).registerItem(itemRegistry);
        (leafShearingAddonItem = new LeafShearingAddonItem()).registerItem(itemRegistry);
        (itemStackTransferAddonPull = new ItemStackTransferAddon(TransferAddon.ActionMode.PULL)).registerItem(itemRegistry);
        (itemStackTransferAddonPush = new ItemStackTransferAddon(TransferAddon.ActionMode.PUSH)).registerItem(itemRegistry);
        (fluidTransferAddonPull = new FluidTransferAddon(TransferAddon.ActionMode.PULL)).registerItem(itemRegistry);
        (fluidTransferAddonPush = new FluidTransferAddon(TransferAddon.ActionMode.PUSH)).registerItem(itemRegistry);

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

        OreDictionary.registerOre("itemRubber", plastic);
        OreDictionary.registerOre("slimeball", pinkSlime);
        OreDictionary.registerOre("slimeballPink", pinkSlime);
        OreDictionary.registerOre("dyeBrown", fertilizer);
        OreDictionary.registerOre("fertilizer", fertilizer);
    }
}
