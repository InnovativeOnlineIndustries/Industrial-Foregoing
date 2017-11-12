package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.WitherBuilderTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class WitherBuilderBlock extends CustomOrientedBlock<WitherBuilderTile> {

    public WitherBuilderBlock() {
        super("wither_builder", WitherBuilderTile.class, Material.ROCK, 20000, 500);
        this.setResistance(Float.MAX_VALUE);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pnp", "sms", "ggg",
                'p', ItemRegistry.plastic,
                'n', Items.NETHER_STAR,
                's', new ItemStack(Items.SKULL, 1, 1),
                'm', MachineCaseItem.INSTANCE,
                'g', Blocks.SOUL_SAND);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power, " + PageText.bold("3") + " Wither Skulls and " + PageText.bold("4") + " soulsand, it will completely build the wither spawning structure in the working area."));
        return pages;
    }
}
