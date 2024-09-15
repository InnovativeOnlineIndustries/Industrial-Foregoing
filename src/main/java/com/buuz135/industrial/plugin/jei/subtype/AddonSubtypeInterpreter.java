package com.buuz135.industrial.plugin.jei.subtype;

import com.hrznstudio.titanium.item.AugmentWrapper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AddonSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        Map<String, Float> attachment = ingredient.getOrDefault(AugmentWrapper.ATTACHMENT, new HashMap<>());
        var result = new StringBuilder();
        attachment.forEach((key, value) -> result.append(key).append("=").append(value).append('-'));
        return result.toString();
    }
}
