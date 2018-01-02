package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.OreProcessorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class OreProcessorBlock extends CustomOrientedBlock<OreProcessorTile> {

    @Getter
    private int essenceFortune;

    public OreProcessorBlock() {
        super("ore_processor", OreProcessorTile.class, Material.ROCK, 1000, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pip", "ama", "brb",
                'p', ItemRegistry.plastic,
                'i', Blocks.PISTON,
                'a', Items.IRON_PICKAXE,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BOOK,
                'r', Items.REDSTONE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceFortune = CustomConfiguration.config.getInt("essenceFortune", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 200, 1, Integer.MAX_VALUE, "Amount of essence needed for each fortune level.");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }


}
