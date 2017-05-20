package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.LavaFabricatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class LavaFabricatorBlock extends CustomOrientedBlock<LavaFabricatorTile> {
    public LavaFabricatorBlock() {
        super("lava_fabricator", LavaFabricatorTile.class, Material.ROCK, 200000, 200000);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pop", "cmc", "brb",
                'p', ItemRegistry.plastic,
                'o', Blocks.OBSIDIAN,
                'c', Items.MAGMA_CREAM,
                'm', TeslaCoreLib.machineCase,
                'b', Items.BLAZE_ROD,
                'r', Blocks.REDSTONE_BLOCK);
    }
}
