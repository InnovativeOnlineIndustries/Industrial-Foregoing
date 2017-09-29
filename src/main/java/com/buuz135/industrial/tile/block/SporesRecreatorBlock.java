package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.SporesRecreatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class SporesRecreatorBlock extends CustomOrientedBlock<SporesRecreatorTile> {

    public SporesRecreatorBlock() {
        super("spores_recreator", SporesRecreatorTile.class, Material.ROCK, 400, 10);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "omo", "pgp",
                'p', ItemRegistry.plastic,
                'o', Blocks.RED_MUSHROOM,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power, " + PageText.bold("1") + " bucket of water and a " + PageText.bold("mushroom") + " of any kind it will grow them internally."));
        return pages;
    }
}
