package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.CropSowerTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class CropSowerBlock extends CustomOrientedBlock<CropSowerTile> {

    public CropSowerBlock() {
        super("crop_sower", CropSowerTile.class, Material.ROCK, 400, 40);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pfp", "ama", "grg",
                'p', ItemRegistry.plastic,
                'f', Items.FLOWER_POT,
                'a', Blocks.PISTON,
                'm', TeslaCoreLib.machineCase,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
