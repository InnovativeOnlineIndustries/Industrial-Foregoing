package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class MycelialGeneratorEmiCategory extends EmiRecipeCategory {

    private final IMycelialGeneratorType type;

    public MycelialGeneratorEmiCategory(ResourceLocation identifier, Item item, IMycelialGeneratorType type) {
        super(identifier, EmiStack.of(item));
        this.type = type;
    }

    @Override
    public Component getName() {
        return Component.translatable("industrialforegoing.jei.category." + type.getName());
    }
}
