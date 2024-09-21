package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public class BioreactorEmiCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "bioreactor");

    public BioreactorEmiCategory() {
        super(ID, EmiStack.of(ModuleGenerator.BIOREACTOR.asItem()));
    }


}
