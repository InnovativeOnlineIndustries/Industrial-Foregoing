package com.buuz135.industrial.plugin.jei.subtype;

import com.buuz135.industrial.utils.IFAttachments;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class InfinitySubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        return ingredient.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L).toString() + "_" + ingredient.getOrDefault(IFAttachments.INFINITY_ITEM_SPECIAL, true).toString();
    }
}
