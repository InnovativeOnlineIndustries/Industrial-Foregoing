package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.ResourcefulFurnaceTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class ResourcefulFurnaceBlock extends CustomOrientedBlock<ResourcefulFurnaceTile> {

    @Getter
    private int experienceMultiplier;

    public ResourcefulFurnaceBlock() {
        super("resourceful_furnace", ResourcefulFurnaceTile.class, Material.ROCK, 8000, 80);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        experienceMultiplier = CustomConfiguration.config.getInt("experienceMultiplier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 50, 1, Integer.MAX_VALUE, "Essence multiplier for the furnace recipes.");

    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this, 1), "pbp", "rmr", "pgp",
                'p', ItemRegistry.plastic,
                'b', Items.BUCKET,
                'r', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

}
