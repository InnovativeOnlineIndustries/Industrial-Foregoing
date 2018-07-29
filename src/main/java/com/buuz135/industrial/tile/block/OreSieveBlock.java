package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.ore.OreSieveTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class OreSieveBlock extends CustomOrientedBlock<OreSieveTile> {

    public OreSieveBlock() {
        super("ore_sieve", OreSieveTile.class, Material.ROCK, 2000, 40);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "psp", "iii", "gmg",
                'p', ItemRegistry.plastic,
                's', ItemRegistry.pinkSlime,
                'i', Blocks.IRON_BARS,
                'g', "gearGold",
                'm', MachineCaseItem.INSTANCE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }
}
