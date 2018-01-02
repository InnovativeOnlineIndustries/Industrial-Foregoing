package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

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

}
