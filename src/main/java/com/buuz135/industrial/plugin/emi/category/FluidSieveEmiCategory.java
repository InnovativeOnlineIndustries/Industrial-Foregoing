package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public class FluidSieveEmiCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_sieve");

    public FluidSieveEmiCategory() {
        super(ID, EmiStack.of(ModuleResourceProduction.FLUID_SIEVING_MACHINE.asItem()));
    }

}
