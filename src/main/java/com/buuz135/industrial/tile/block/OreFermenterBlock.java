package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.tile.ore.OreFermenterTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class OreFermenterBlock extends CustomOrientedBlock<OreFermenterTile> {

    public OreFermenterBlock() {
        super("ore_fermenter", OreFermenterTile.class, Material.ROCK, 1000, 40);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "iwi", "wgw", "imi",
                'i', Blocks.IRON_BARS,
                'w', "logWood",
                'g', "gearIron",
                'm', MachineCaseItem.INSTANCE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }
}
