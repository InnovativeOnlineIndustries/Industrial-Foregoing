package com.buuz135.industrial.proxy;

import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.item.LaserLensItem;
import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.item.addon.RedstoneInvertedAddon;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;


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
    public static RedstoneInvertedAddon redstoneInvertedAddon;

    public static void registerItems() {
        (tinyDryRubber = new IFCustomItem("tinydryrubber")).register();
        (dryRubber = new IFCustomItem("dryrubber") {
            @Override
            public IRecipe getRecipe() {
                return new ShapelessRecipes(new ItemStack(this), Arrays.asList(new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber), new ItemStack(tinyDryRubber)));
            }
        }).register();
        (plastic = new IFCustomItem("plastic")).register();
        OreDictionary.registerOre("itemRubber", plastic);
        GameRegistry.addSmelting(dryRubber, new ItemStack(plastic), 0);
        (fertilizer = new IFCustomItem("fertilizer")).register();
        (meatFeederItem = new MeatFeederItem()).register();
        (mobImprisonmentToolItem = new MobImprisonmentToolItem()).register();

        (laserLensItem = new LaserLensItem()).register();

        (adultFilterAddomItem = new AdultFilterAddonItem()).register();
        (rangeAddonItem = new RangeAddonItem()).register();
        (redstoneInvertedAddon = new RedstoneInvertedAddon()).register();
    }
}
