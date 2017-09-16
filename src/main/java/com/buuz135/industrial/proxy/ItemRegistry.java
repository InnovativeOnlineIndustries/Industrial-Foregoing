package com.buuz135.industrial.proxy;

import com.buuz135.industrial.item.*;
import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
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

    public static ItemStraw strawItem;

    public static AdultFilterAddonItem adultFilterAddomItem;
    public static RangeAddonItem rangeAddonItem;


    public static void registerItems(IForgeRegistry<Item> itemRegistry) {
        (tinyDryRubber = new IFCustomItem("tinydryrubber")).register(itemRegistry);
        (dryRubber = new IFCustomItem("dryrubber")).register(itemRegistry);
        RecipeUtils.addShapelessRecipe(new ItemStack(dryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber));
        (plastic = new IFCustomItem("plastic")).register(itemRegistry);
        OreDictionary.registerOre("itemRubber", plastic);
        GameRegistry.addSmelting(dryRubber, new ItemStack(plastic), 0);
        (fertilizer = new IFCustomItem("fertilizer")).register(itemRegistry);
        (meatFeederItem = new MeatFeederItem()).register(itemRegistry);
        (mobImprisonmentToolItem = new MobImprisonmentToolItem()).register(itemRegistry);
        (laserLensItem = new LaserLensItem(false)).register(itemRegistry);
        (laserLensItem_inverted = new LaserLensItem(true)).register(itemRegistry);
        (strawItem = new ItemStraw()).register(itemRegistry);
        (pinkSlime = new IFCustomItem("pink_slime")).register(itemRegistry);

        (adultFilterAddomItem = new AdultFilterAddonItem()).registerItem(itemRegistry);
        (rangeAddonItem = new RangeAddonItem()).registerItem(itemRegistry);

        meatFeederItem.createRecipe();
        mobImprisonmentToolItem.createRecipe();
        laserLensItem.createRecipe();
        laserLensItem_inverted.createRecipe();
        adultFilterAddomItem.createRecipe();
        rangeAddonItem.createRecipe();
    }
}
