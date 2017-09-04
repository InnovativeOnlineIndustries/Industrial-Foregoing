package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.generator.BioReactorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;


public class BioReactorBlock extends CustomOrientedBlock<BioReactorTile> {

    private int baseAmount;

    public BioReactorBlock() {
        super("bioreactor", BioReactorTile.class, Material.ROCK, 2000, 10);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        baseAmount = CustomConfiguration.config.getInt("baseBiofuel", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 80, 1, 4000, "Base biofuel amount in mb");
    }


    public int getBaseAmount() {
        return baseAmount;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "sms", "bcb",
                'p', ItemRegistry.plastic,
                'e', Items.FERMENTED_SPIDER_EYE,
                's', Items.SLIME_BALL,
                'm', MachineCaseItem.INSTANCE,
                'b', Items.BRICK,
                'c', Items.SUGAR);
    }
}
