package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentRefinerTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class EnchantmentRefinerBlock extends CustomOrientedBlock<EnchantmentRefinerTile> {

    public EnchantmentRefinerBlock() {
        super("enchantment_refiner", EnchantmentRefinerTile.class, Material.ROCK, 400, 10);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1), "pep", "bmn", "pgp",
                'p', ItemRegistry.plastic,
                'e', Items.ENDER_PEARL,
                'b', Items.BOOK,
                'm', TeslaCoreLib.machineCase,
                'n', Items.ENCHANTED_BOOK,
                'g', "gearDiamond");
    }

}
