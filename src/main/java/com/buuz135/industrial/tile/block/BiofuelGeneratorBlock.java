package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.BiofuelGeneratorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class BiofuelGeneratorBlock extends CustomOrientedBlock<BiofuelGeneratorTile> {

    public BiofuelGeneratorBlock() {
        super("biofuel_generator", BiofuelGeneratorTile.class, Material.ROCK, 0, 0);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pfp", "imi", "rir",
                'p', ItemRegistry.plastic,
                'f', Blocks.FURNACE,
                'i', Blocks.PISTON,
                'm', TeslaCoreLib.machineCase,
                'r', Items.BLAZE_ROD);
    }
}
