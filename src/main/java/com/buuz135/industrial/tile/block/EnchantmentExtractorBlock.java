package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.EnchantmentExtractorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class EnchantmentExtractorBlock extends CustomOrientedBlock<EnchantmentExtractorTile> {

    public EnchantmentExtractorBlock() {
        super("enchantment_extractor", EnchantmentExtractorTile.class, Material.ROCK, 5000, 100);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pnp", "bmb", "dgd",
                'p', ItemRegistry.plastic,
                'n', Blocks.NETHER_BRICK,
                'b', Items.BOOK,
                'm', TeslaCoreLib.machineCase,
                'd', "gemDiamond",
                'g', "gearGold");
    }
}
