package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.magic.PotionEnervatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class PotionEnervatorBlock extends CustomOrientedBlock<PotionEnervatorTile> {

    public PotionEnervatorBlock() {
        super("potion_enervator", PotionEnervatorTile.class, Material.ROCK, 5000, 40);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pbp", "gmg", "rgr",
                'p', ItemRegistry.plastic,
                'b', Items.BREWING_STAND,
                'g', "gearGold",
                'm', TeslaCoreLib.machineCase,
                'r', Items.REPEATER);
    }
}
