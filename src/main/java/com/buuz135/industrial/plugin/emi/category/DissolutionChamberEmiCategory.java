package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public class DissolutionChamberEmiCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "dissolution_chamber");

    public DissolutionChamberEmiCategory() {
        super(ID, EmiStack.of(ModuleCore.DISSOLUTION_CHAMBER.asItem()));
    }


}
