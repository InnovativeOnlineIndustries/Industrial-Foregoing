package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.AnimalStockIncreaserTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class AnimalStockIncreaserBlock extends CustomOrientedBlock<AnimalStockIncreaserTile> {

    public AnimalStockIncreaserBlock() {
        super("animal_stock_increaser", AnimalStockIncreaserTile.class, Material.ROCK, 400, 20);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pgp", "cmc", "dad",
                'p', ItemRegistry.plastic,
                'g', Items.GOLDEN_APPLE,
                'c', Items.GOLDEN_CARROT,
                'm', TeslaCoreLib.machineCase,
                'd', "dyePurple",
                'a', "gearIron");
    }
}
