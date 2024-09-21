package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public class StoneWorkGeneratorEmiCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "stone_work_generator");

    public StoneWorkGeneratorEmiCategory() {
        super(ID, EmiStack.of(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY.asItem()));
    }


}
