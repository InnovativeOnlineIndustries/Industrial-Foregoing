package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.BlockPlacerTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class BlockPlacerBlock extends CustomOrientedBlock<BlockPlacerTile> {

    public BlockPlacerBlock() {
        super("block_placer", BlockPlacerTile.class, Material.ROCK, 100, 20);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pdp", "dmd", "prp",
                'p', ItemRegistry.plastic,
                'd', Blocks.DROPPER,
                'm', TeslaCoreLib.machineCase,
                'r', Items.REDSTONE);
    }
}
