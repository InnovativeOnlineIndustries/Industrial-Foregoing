package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
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

import java.util.List;

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

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("It acts like a normal " + PageText.bold("furnace") + " but it can burn " + PageText.bold("3") + " items at a time and will produce a bit of " + PageText.bold("Essence") + " each time a item is smelted."));
        return pages;
    }
}
