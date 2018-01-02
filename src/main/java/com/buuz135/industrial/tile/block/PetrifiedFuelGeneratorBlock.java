package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.PetrifiedFuelGeneratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class PetrifiedFuelGeneratorBlock extends CustomOrientedBlock<PetrifiedFuelGeneratorTile> {

    @Getter
    private int timeModifier;

    public PetrifiedFuelGeneratorBlock() {
        super("petrified_fuel_generator", PetrifiedFuelGeneratorTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this, 1), "pdp", "gmg", "pfp",
                'p', ItemRegistry.plastic,
                'd', "gemDiamond",
                'g', "gearGold",
                'm', MachineCaseItem.INSTANCE,
                'f', Blocks.FURNACE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        timeModifier = CustomConfiguration.config.getInt("timeModifier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 20, 1, Integer.MAX_VALUE, "Time modifier for the burning time. (FuelBurningTime/%value%)");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.GENERATORS;
    }

}
