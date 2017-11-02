package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.CropRecolectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;
import org.jetbrains.annotations.NotNull;

public class LeafShearingAddonItem extends CustomAddon {

    public LeafShearingAddonItem() {
        super("leaf_shearing");
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "gpg", "gmg", "gpg",
                'g', "nuggetGold",
                'p', ItemRegistry.plastic,
                'm', Items.SHEARS);
    }

    @Override
    public boolean canBeAddedTo(@NotNull SidedTileEntity machine) {
        return machine instanceof CropRecolectorTile && !((CropRecolectorTile) machine).hasShearingAddon();
    }
}
