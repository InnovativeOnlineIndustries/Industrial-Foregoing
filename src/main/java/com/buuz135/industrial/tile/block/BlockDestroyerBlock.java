package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.BlockDestroyerTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class BlockDestroyerBlock extends CustomOrientedBlock<BlockDestroyerTile> {

    public BlockDestroyerBlock() {
        super("block_destroyer", BlockDestroyerTile.class, Material.ROCK, 100, 20);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pgp", "ams", "iri",
                'p', ItemRegistry.plastic,
                'g', "gearGold",
                'a', Items.IRON_PICKAXE,
                'm', TeslaCoreLib.machineCase,
                's', Items.IRON_SHOVEL,
                'i', "gearIron",
                'r', Items.REDSTONE);
    }
}
