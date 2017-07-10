package com.buuz135.industrial.proxy;

import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.item.LaserLensItem;
import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
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

        (laserLensItem = new LaserLensItem()).register(itemRegistry);

        (adultFilterAddomItem = new AdultFilterAddonItem()).register(itemRegistry);
        (rangeAddonItem = new RangeAddonItem()).register(itemRegistry);

        meatFeederItem.createRecipe();
        mobImprisonmentToolItem.createRecipe();
        laserLensItem.createRecipe();
        adultFilterAddomItem.createRecipe();
        rangeAddonItem.createRecipe();
    }
}
