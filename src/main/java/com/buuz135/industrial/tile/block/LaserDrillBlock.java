package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.LaserDrillTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class LaserDrillBlock extends CustomOrientedBlock<LaserDrillTile> {

    public LaserDrillBlock() {
        super("laser_drill", LaserDrillTile.class, Material.ROCK, 3000, 100);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "plp", "gwg", "omo",
                'p', ItemRegistry.plastic,
                'l', ItemRegistry.laserLensItem,
                'g', "blockGlassColorless",
                'w', Blocks.GLOWSTONE,
                'o', "gearDiamond",
                'm', MachineCaseItem.INSTANCE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When placed " + PageText.bold("1") + " block away of a " + PageText.bold("Laser Base") + " it will charge it with power.\n\nIf it is wrongly placed it will have a name tag on top telling you so."));
        return pages;
    }
}
