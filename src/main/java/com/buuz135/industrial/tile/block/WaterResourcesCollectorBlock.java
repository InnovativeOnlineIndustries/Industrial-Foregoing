package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.WaterResourcesCollectorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class WaterResourcesCollectorBlock extends CustomOrientedBlock<WaterResourcesCollectorTile> {

    public WaterResourcesCollectorBlock() {
        super("water_resources_collector", WaterResourcesCollectorTile.class, Material.ROCK, 5000, 80);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pfp", "bmb", "grg",
                'p', ItemRegistry.plastic,
                'f', Items.FISHING_ROD,
                'b', Items.BUCKET,
                'm', TeslaCoreLib.machineCase,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
