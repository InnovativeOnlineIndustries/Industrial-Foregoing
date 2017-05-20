package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.AnimalResourceHarvesterTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class AnimalResourceHarvesterBlock extends CustomOrientedBlock<AnimalResourceHarvesterTile> {

    public AnimalResourceHarvesterBlock() {
        super("animal_resource_harvester", AnimalResourceHarvesterTile.class, Material.ROCK, 400, 20);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "ppp", "sbs", "gmg",
                'p', ItemRegistry.plastic,
                's', Items.SHEARS,
                'b', Items.BUCKET,
                'g', "gearGold",
                'm', TeslaCoreLib.machineCase);
    }
}
