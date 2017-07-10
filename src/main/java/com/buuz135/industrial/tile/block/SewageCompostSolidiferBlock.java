package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.SewageCompostSolidifierTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class SewageCompostSolidiferBlock extends CustomOrientedBlock<SewageCompostSolidifierTile> {

    public SewageCompostSolidiferBlock() {
        super("sewage_composter_solidifier", SewageCompostSolidifierTile.class, Material.ROCK, 1000, 10);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "imi", "brb",
                'p', ItemRegistry.plastic,
                'f', Blocks.FURNACE,
                'i', Blocks.PISTON,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BRICK,
                'r', Items.REDSTONE);
    }
}
