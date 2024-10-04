package com.buuz135.industrial.plugin.jei.subtype;

import com.buuz135.industrial.utils.IFAttachments;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfinitySubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return List.of(
            ingredient.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L),
            ingredient.getOrDefault(IFAttachments.INFINITY_ITEM_SPECIAL, true)
        );
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return ingredient.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L).toString() + "_" + ingredient.getOrDefault(IFAttachments.INFINITY_ITEM_SPECIAL, true).toString();
    }
}
