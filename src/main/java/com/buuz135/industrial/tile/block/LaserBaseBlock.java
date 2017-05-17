package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class LaserBaseBlock extends CustomOrientedBlock<LaserBaseTile> {

    private int workNeeded;
    private Multimap<Integer, ItemStackWeightedItem> coloreOres;
    private int lenseChanceIncrease;

    public LaserBaseBlock() {
        super("laser_base", LaserBaseTile.class, Material.ROCK, 100000, 100);
        coloreOres = ArrayListMultimap.create();
    }


    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        workNeeded = CustomConfiguration.config.getInt("workNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 100, 1, Integer.MAX_VALUE, "Amount of work needed to produce an ore");
        lenseChanceIncrease = CustomConfiguration.config.getInt("lenseChanceIncrease", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "How much weight each lense increases to the ore");
        String[] entries = CustomConfiguration.config.getStringList("ores", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), new String[]{
                "4 oreGold 0 6",
                "12 oreIron 0 10",
                "15 oreCoal 0 12",
                "11 oreLapis 0 8",
                "3 oreDiamond 0 4",
                "14 oreRedstone 0 6",
                "0 oreQuartz 0 4",
                "5 oreEmerald 0 2"
        }, "List of ores, format 'lensMetada itemID/oredictEntry itemMeta weight'");//TODO add modded ores
        for (String s : entries) {
            System.out.println("adding" + s);
            String[] temp = s.split(" ");
            int lens = Integer.parseInt(temp[0]);
            int meta = Integer.parseInt(temp[2]);
            int weight = Integer.parseInt(temp[3]);
            if (temp[1].contains(":")) {
                if (Item.getByNameOrId(s) != null)
                    coloreOres.put(lens, new ItemStackWeightedItem(new ItemStack(Item.getByNameOrId(s), meta), weight));
            } else {
                for (ItemStack stack : OreDictionary.getOres(temp[1])) {
                    coloreOres.put(lens, new ItemStackWeightedItem(stack, weight));
                }
            }
        }
    }

    public int getWorkNeeded() {
        return workNeeded;
    }

    public Multimap<Integer, ItemStackWeightedItem> getColoreOres() {
        return coloreOres;
    }

    public int getLenseChanceIncrease() {
        return lenseChanceIncrease;
    }


}



