package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobDuplicatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class MobDuplicatorBlock extends CustomOrientedBlock<MobDuplicatorTile> {

    public MobDuplicatorBlock() {
        super("mob_duplicator", MobDuplicatorTile.class, Material.ROCK, 5000, 80);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pwp", "cmc", "ere",
                'p', ItemRegistry.plastic,
                'w', Items.NETHER_WART,
                'c', Items.MAGMA_CREAM,
                'm', TeslaCoreLib.machineCase,
                'e', "gemEmerald",
                'r', Items.REDSTONE);
    }
}
