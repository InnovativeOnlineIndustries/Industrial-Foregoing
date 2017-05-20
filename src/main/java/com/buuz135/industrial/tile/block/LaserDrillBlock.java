package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.LaserDrillTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class LaserDrillBlock extends CustomOrientedBlock<LaserDrillTile> {

    public LaserDrillBlock() {
        super("laser_drill", LaserDrillTile.class, Material.ROCK, 3000, 100);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "plp", "gwg", "omo",
                'p', ItemRegistry.plastic,
                'l', ItemRegistry.laserLensItem,
                'g', "blockGlassColorless",
                'w', Blocks.GLOWSTONE,
                'o', "gearGold",
                'm', TeslaCoreLib.machineCase);
    }
}
