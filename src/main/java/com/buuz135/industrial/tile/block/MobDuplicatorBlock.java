package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobDuplicatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class MobDuplicatorBlock extends CustomOrientedBlock<MobDuplicatorTile> {

    public MobDuplicatorBlock() {
        super("mob_duplicator", MobDuplicatorTile.class, Material.ROCK, 5000, 80);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "cmc", "ere",
                'p', ItemRegistry.plastic,
                'w', Items.NETHER_WART,
                'c', Items.MAGMA_CREAM,
                'm', MachineCaseItem.INSTANCE,
                'e', "gemEmerald",
                'r', Items.REDSTONE);
    }
}
