package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.BlockDestroyerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class BlockDestroyerBlock extends CustomOrientedBlock<BlockDestroyerTile> {

    public BlockDestroyerBlock() {
        super("block_destroyer", BlockDestroyerTile.class, Material.ROCK, 100, 20);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "ams", "iri",
                'p', ItemRegistry.plastic,
                'g', "gearGold",
                'a', Items.IRON_PICKAXE,
                'm', MachineCaseItem.INSTANCE,
                's', Items.IRON_SHOVEL,
                'i', "gearIron",
                'r', Items.REDSTONE);
    }
}
