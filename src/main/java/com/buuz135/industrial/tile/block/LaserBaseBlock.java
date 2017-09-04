package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class LaserBaseBlock extends CustomOrientedBlock<LaserBaseTile> {

    private int workNeeded;
    //private Multimap<Integer, ItemStackWeightedItem> coloreOres;
    private int lenseChanceIncrease;

    public LaserBaseBlock() {
        super("laser_base", LaserBaseTile.class, Material.ROCK, 100000, 100);
    }


    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        workNeeded = CustomConfiguration.config.getInt("workNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 100, 1, Integer.MAX_VALUE, "Amount of work needed to produce an ore");
        lenseChanceIncrease = CustomConfiguration.config.getInt("lenseChanceIncrease", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "How much weight each lense increases to the ore");
    }

    public int getWorkNeeded() {
        return workNeeded;
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



