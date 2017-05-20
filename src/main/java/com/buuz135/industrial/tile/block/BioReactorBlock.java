package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.BioReactorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

import java.util.ArrayList;
import java.util.List;


public class BioReactorBlock extends CustomOrientedBlock<BioReactorTile> {

    private List<ItemStack> itemsAccepted;
    private int baseAmount;

    public BioReactorBlock() {
        super("bioreactor", BioReactorTile.class, Material.ROCK, 2000, 10);
        itemsAccepted = new ArrayList<>();
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        baseAmount = CustomConfiguration.config.getInt("baseBiofuel", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 80, 1, 4000, "Base biofuel amount in mb");
        String[] items = CustomConfiguration.config.getStringList("acceptedItems", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), new String[]{
                "minecraft:wheat_seeds",
                "minecraft:pumpkin_seeds",
                "minecraft:melon_seeds",
                "minecraft:beetroot_seeds",
                "treeSapling",
                "minecraft:carrot",
                "minecraft:potato",
                "minecraft:nether_wart",
                "minecraft:brown_mushroom",
                "minecraft:red_mushroom",
                "dye",
                "minecraft:chorus_flower"
        }, "It can accept oreDictionary entries and item ids");
        for (String s : items) {
            if (s.contains(":")) {
                if (Item.getByNameOrId(s) != null) itemsAccepted.add(new ItemStack(Item.getByNameOrId(s)));
            } else {
                itemsAccepted.addAll(OreDictionary.getOres(s));
            }
        }
    }

    public List<ItemStack> getItemsAccepted() {
        return itemsAccepted;
    }

    public int getBaseAmount() {
        return baseAmount;
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pep", "sms", "bcb",
                'p', ItemRegistry.plastic,
                'e', Items.FERMENTED_SPIDER_EYE,
                's', Items.SLIME_BALL,
                'm', TeslaCoreLib.machineCase,
                'b', Items.BRICK,
                'c', Items.SUGAR);
    }
}
