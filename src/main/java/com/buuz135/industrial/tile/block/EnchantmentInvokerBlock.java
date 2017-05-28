package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentInvokerTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class EnchantmentInvokerBlock extends CustomOrientedBlock<EnchantmentInvokerTile> {

    public EnchantmentInvokerBlock() {
        super("enchantment_invoker", EnchantmentInvokerTile.class, Material.ROCK, 4000, 80);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pbp", "dmd", "ooo",
                'p', ItemRegistry.plastic,
                'b', Items.BOOK,
                'd', "gemDiamond",
                'm', TeslaCoreLib.machineCase,
                'o', Blocks.OBSIDIAN);
    }
}
