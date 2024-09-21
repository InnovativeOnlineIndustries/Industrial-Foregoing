package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.emi.recipe.FluidExtractorEmiRecipe;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class FluidExtractorEmiCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_extractor");

    public FluidExtractorEmiCategory() {
        super(ID, EmiStack.of(ModuleCore.FLUID_EXTRACTOR.asItem()));
    }

    @Override
    public @Nullable Comparator<EmiRecipe> getSort() {
        return (o1, o2) -> {
            FluidExtractorEmiRecipe e1 = (FluidExtractorEmiRecipe) o1;
            FluidExtractorEmiRecipe e2 = (FluidExtractorEmiRecipe) o2;
            return Integer.compare(e2.getRecipe().value().output.getAmount(), e1.getRecipe().value().output.getAmount());
        };
    }
}
