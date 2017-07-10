package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.LatexProcessingUnitTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class LatexProcessingUnitBlock extends CustomOrientedBlock<LatexProcessingUnitTile> {

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", LatexProcessingUnitTile.class, Material.ROCK, 200, 5);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "prp", "fmf", "pgp",
                'p', "ingotIron",
                'r', Items.REDSTONE,
                'f', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron");
    }
}
