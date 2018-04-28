package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.EnergyFieldProviderTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class EnergyFieldProviderBlock extends CustomAreaOrientedBlock<EnergyFieldProviderTile> {

    public EnergyFieldProviderBlock() {
        super("energy_field_provider", EnergyFieldProviderTile.class, Material.ROCK, 1, 1, RangeType.UP, 11, 3, true);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "gug", "gtg", "rdr",
                'g', "ingotGold",
                'u', ItemRegistry.energyFieldAddon,
                't', MachineCaseItem.INSTANCE,
                'r', Items.REPEATER,
                'd', "gearDiamond");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

}
