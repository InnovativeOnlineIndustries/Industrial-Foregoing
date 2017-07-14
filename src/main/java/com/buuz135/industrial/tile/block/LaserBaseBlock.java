package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import com.buuz135.industrial.utils.RecipeUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.ndrei.teslacorelib.items.MachineCaseItem;

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
                "5 oreEmerald 0 2",
                "13 oreUranium 0 3",
                "4 oreSulfur 0 8",
                "10 oreGalena 0 6",
                "0 oreIridium 0 2",
                "14 oreRuby 0 7",
                "11 oreSapphire 0 7",
                "12 oreBauxite 0 5",
                "12 orePyrite 0 5",
                "14 oreCinnabar 0 8",
                "12 oreSphalerite 0 4",
                "15 oreTungsten 0 3",
                "0 oreSheldonite 0 1",
                "3 orePlatinum 0 2",
                "13 orePeridot 0 7",
                "11 oreSoladite 0 4",
                "14 oreTetrahedrite 0 4",
                "8 oreTin 0 8",
                "10 oreLead 0 5",
                "7 oreSilver 0 5",
                "1 oreCopper 0 10"
        }, "List of ores, format 'lensMetada itemID/oredictEntry itemMeta weight'");//TODO add modded ores
        for (String s : entries) {
            String[] temp = s.split(" ");
            int lens = Integer.parseInt(temp[0]);
            int meta = Integer.parseInt(temp[2]);
            int weight = Integer.parseInt(temp[3]);
            if (temp[1].contains(":")) {
                if (Item.getByNameOrId(temp[1]) != null)
                    coloreOres.put(lens, new ItemStackWeightedItem(new ItemStack(Item.getByNameOrId(temp[1]), 1,meta), weight));
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

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "gwg", "dmd",
                'p', ItemRegistry.plastic,
                'w', Blocks.GLOWSTONE,
                'g', "gearGold",
                'd', "gearDiamond",
                'm', MachineCaseItem.INSTANCE);
    }
}



