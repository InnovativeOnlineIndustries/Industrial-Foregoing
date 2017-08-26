package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.tile.mob.MobDuplicatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class MobDuplicatorBlock extends CustomOrientedBlock<MobDuplicatorTile> {

    public int essenceNeeded;

    public MobDuplicatorBlock() {
        super("mob_duplicator", MobDuplicatorTile.class, Material.ROCK, 5000, 80);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "cmc", "ere",
                'p', "itemRubber",
                'w', Items.NETHER_WART,
                'c', Items.MAGMA_CREAM,
                'm', MachineCaseItem.INSTANCE,
                'e', "gemEmerald",
                'r', Items.REDSTONE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceNeeded = CustomConfiguration.config.getInt("essenceNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 12, 1, Integer.MAX_VALUE, "Essence needed based on mob's health (mobHealth*essenceNeeded)");

    }
}
