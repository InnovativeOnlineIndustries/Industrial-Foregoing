package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.FluidPumpTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class FluidPumpBlock extends CustomOrientedBlock<FluidPumpTile> {

    public FluidPumpBlock() {
        super("fluid_pump", FluidPumpTile.class, Material.ROCK, 1000, 40);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "lmw", "pgp",
                'p', ItemRegistry.plastic,
                'b', Items.BUCKET,
                'l', Items.LAVA_BUCKET,
                'w', Items.WATER_BUCKET,
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
        pages.add(0, new PageText("When placed above a fluid, the Fluid Pump will take that fluid as a " + PageText.bold("filter") + " and begin to drain all fluids of the same type in the working area, it will search all the fluids " + PageText.bold("below") + " the fluids in the working area and drain them too. \n\nAll the drained fluids will be replaced with " + PageText.bold("cobblestone") + "."));
        return pages;
    }
}
