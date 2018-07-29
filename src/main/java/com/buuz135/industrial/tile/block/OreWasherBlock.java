package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.ore.OreWasherTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class OreWasherBlock extends CustomOrientedBlock<OreWasherTile> {

    public OreWasherBlock() {
        super("ore_washer", OreWasherTile.class, Material.ROCK, 8000, 60);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "rmr", "cgc",
                'p', ItemRegistry.pinkSlime,
                'f', ItemRegistry.meatFeederItem,
                'r', ItemRegistry.plastic,
                'm', MachineCaseItem.INSTANCE,
                'c', new ItemStack(BlockRegistry.blockConveyor, 1, OreDictionary.WILDCARD_VALUE),
                'g', "gearDiamond");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }
}
