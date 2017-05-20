package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentAplicatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class EnchantmentAplicatorBlock extends CustomOrientedBlock<EnchantmentAplicatorTile> {

    public EnchantmentAplicatorBlock() {
        super("enchantment_aplicator", EnchantmentAplicatorTile.class, Material.ROCK, 5000, 100);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "ppp", "ama", "gag",
                'p', ItemRegistry.plastic,
                'a', Blocks.ANVIL,
                'm', TeslaCoreLib.machineCase,
                'g', "gearIron");
    }
}
