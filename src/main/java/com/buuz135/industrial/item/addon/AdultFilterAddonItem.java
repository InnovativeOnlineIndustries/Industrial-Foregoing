package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.api.IAcceptsAdultFilter;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class AdultFilterAddonItem extends CustomAddon {

    public AdultFilterAddonItem() {
        super("adult_filter");
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return machine instanceof IAcceptsAdultFilter && !((IAcceptsAdultFilter) machine).hasAddon();
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "gpg", "gmg", "gpg",
                'g', "nuggetGold",
                'p', ItemRegistry.plastic,
                'm', "egg");
    }
}
