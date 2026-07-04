package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public class SludgeRefinerEmiCategory extends EmiRecipeCategory {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sludge_refiner");

    public SludgeRefinerEmiCategory() {
        super(ID, EmiStack.of(ModuleResourceProduction.SLUDGE_REFINER.asItem()));
    }
}
