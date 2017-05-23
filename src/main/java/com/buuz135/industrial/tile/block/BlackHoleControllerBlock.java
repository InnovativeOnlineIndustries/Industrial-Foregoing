package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleControllerTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class BlackHoleControllerBlock extends CustomOrientedBlock<BlackHoleControllerTile> {

    public BlackHoleControllerBlock() {
        super("black_hole_controller", BlackHoleControllerTile.class, Material.ROCK, 0, 0);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pdp", "ece","pmp",
                'p', ItemRegistry.plastic,
                'd', Blocks.DIAMOND_BLOCK,
                'e', Items.ENDER_EYE,
                'c', Blocks.ENDER_CHEST,
                'm', TeslaCoreLib.machineCase);
    }
}
