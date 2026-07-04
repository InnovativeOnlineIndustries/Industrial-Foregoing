package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public class SporesRecreatorEmiCategory extends EmiRecipeCategory {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "spores_recreator");

    public SporesRecreatorEmiCategory() {
        super(ID, EmiStack.of(ModuleResourceProduction.SPORES_RECREATOR.asItem()));
    }
}
