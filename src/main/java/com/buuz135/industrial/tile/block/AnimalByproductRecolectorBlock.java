package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.tile.agriculture.AnimalByproductRecolectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;


public class AnimalByproductRecolectorBlock extends CustomOrientedBlock<AnimalByproductRecolectorTile> {

    private int sewageAdult;
    private int sewageBaby;
    private int maxSludgeOperation;

    public AnimalByproductRecolectorBlock() {
        super("animal_byproduct_recolector", AnimalByproductRecolectorTile.class, Material.ROCK, 40, 2);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        sewageAdult = CustomConfiguration.config.getInt("sewageAdult", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 15, 1, Integer.MAX_VALUE, "Sewage produced by an adult animal");
        sewageBaby = CustomConfiguration.config.getInt("sewageBaby", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "Sewage produced by a baby animal");
        maxSludgeOperation = CustomConfiguration.config.getInt("maxSludgeOperation", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 150, 1, Integer.MAX_VALUE, "Max sludge produced in an operation");
    }

    public int getSewageAdult() {
        return sewageAdult;
    }

    public int getSewageBaby() {
        return sewageBaby;
    }

    public int getMaxSludgeOperation() {
        return maxSludgeOperation;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "bmb", "brb",
                'p', "itemRubber",
                'e', Items.BUCKET,
                'b', Items.BRICK,
                'm', MachineCaseItem.INSTANCE,
                'r', Items.REDSTONE);
    }
}
