package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.CropEnrichMaterialInjectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class CropEnrichMaterialInjectorBlock extends CustomOrientedBlock<CropEnrichMaterialInjectorTile> {

    public CropEnrichMaterialInjectorBlock() {
        super("crop_enrich_material_injector", CropEnrichMaterialInjectorTile.class, Material.ROCK, 400, 40);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "lml", "grg",
                'p', ItemRegistry.plastic,
                'b', Items.GLASS_BOTTLE,
                'l', Items.LEATHER,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearIron",
                'r', Items.REDSTONE);
    }
}
