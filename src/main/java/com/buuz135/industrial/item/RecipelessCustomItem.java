package com.buuz135.industrial.item;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;

import java.util.function.Consumer;

public class RecipelessCustomItem extends IFCustomItem {

    public RecipelessCustomItem(String name, ItemGroup group, Properties builder) {
        super(name, group, builder);
    }

    public RecipelessCustomItem(String name, ItemGroup group) {
        super(name, group);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }
}
