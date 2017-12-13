package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class MaterialStoneWorkFactoryBlock extends CustomOrientedBlock<MaterialStoneWorkFactoryTile> {

    public MaterialStoneWorkFactoryBlock() {
        super("material_stonework_factory", MaterialStoneWorkFactoryTile.class, Material.ROCK, 400, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pip", "amf", "lrw",
                'p', ItemRegistry.plastic,
                'i', Blocks.CRAFTING_TABLE,
                'a', Items.IRON_PICKAXE,
                'f', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'l', Items.LAVA_BUCKET,
                'w', Items.WATER_BUCKET,
                'r', ItemRegistry.pinkSlime);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will start generating " + PageText.bold("cobblestone") + ". It has " + PageText.bold("4") + " operations that the machine can do. It can " + PageText.bold("smelt") + " items, " + PageText.bold("2x2") + " craft them, it can " + PageText.bold("grind") + " them and it can " + PageText.bold("3x3") + " craft them.\n\nFor example, the first slot has cobble and the first operation is 'Smelt', then the cobble will turn into stone, the second operation is 2x2 craft, then the stone will turn into stone bricks, etc."));
        return pages;
    }
}
