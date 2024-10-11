package com.buuz135.industrial.plugin.jei.subtype;

import com.hrznstudio.titanium.item.AugmentWrapper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AddonSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(AugmentWrapper.ATTACHMENT);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        Map<String, Float> attachment = ingredient.getOrDefault(AugmentWrapper.ATTACHMENT, new HashMap<>());
        var result = new StringBuilder();
        attachment.forEach((key, value) -> result.append(key).append("=").append(value).append('-'));
        return result.toString();
    }
}
