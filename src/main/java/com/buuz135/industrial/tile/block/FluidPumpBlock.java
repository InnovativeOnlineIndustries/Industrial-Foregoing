package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.FluidPumpTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class FluidPumpBlock extends CustomAreaOrientedBlock<FluidPumpTile> {

    @Getter
    private boolean replaceFluidWithCobble;

    public FluidPumpBlock() {
        super("fluid_pump", FluidPumpTile.class, Material.ROCK, 1000, 40, RangeType.UP, 16, 1, true);
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
    public void getMachineConfig() {
        super.getMachineConfig();
        replaceFluidWithCobble = CustomConfiguration.config.getBoolean("replaceFluidWithCobble", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), true, "Fluid pump should replace the picked up fluid with cobblestone");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

}
