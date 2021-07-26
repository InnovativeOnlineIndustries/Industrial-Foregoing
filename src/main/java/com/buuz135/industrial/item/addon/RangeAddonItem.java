/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import com.hrznstudio.titanium.item.BasicItem.Key;
import net.minecraft.world.item.Item.Properties;

public class RangeAddonItem extends IFCustomItem {

    private static Item[] MATERIALS = new Item[]{Items.COBBLESTONE, Items.LAPIS_LAZULI, Items.BONE_MEAL, Items.IRON_NUGGET, Items.BLAZE_POWDER, Items.GOLD_NUGGET, Items.IRON_INGOT, Items.GOLD_INGOT, Items.QUARTZ, Items.DIAMOND, Items.POPPED_CHORUS_FRUIT, Items.EMERALD};

    public static final IAugmentType RANGE = () -> "Range";

    private int tier;

    public RangeAddonItem(int tier, CreativeModeTab group) {
        super("range_addon" + tier, group, new Properties().stacksTo(16));
        this.tier = tier;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        AugmentWrapper.setType(stack, RANGE, tier);
    }

    @Override
    public String getDescriptionId() {
        return new TranslatableComponent("item.industrialforegoing.addon").getString() + new TranslatableComponent("item.industrialforegoing.range_addon").getString() + "Tier " + (tier + 1) + " ";
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        tooltip.add(new TextComponent(ChatFormatting.GRAY + new TranslatableComponent("text.industrialforegoing.extra_range").getString() + "+" + (tier + 1)));
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(getRegistryName(), new Ingredient.Value[]{
                new Ingredient.ItemValue(new ItemStack(Items.REDSTONE)),
                new Ingredient.ItemValue(new ItemStack(Items.REDSTONE)),
                new Ingredient.ItemValue(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.ItemValue(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.ItemValue(new ItemStack(MATERIALS[tier])),
                new Ingredient.ItemValue(new ItemStack(MATERIALS[tier])),
                new Ingredient.ItemValue(new ItemStack(MATERIALS[tier])),
                new Ingredient.ItemValue(new ItemStack(MATERIALS[tier]))
        }, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 1000), 200, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            AugmentWrapper.setType(stack, RANGE, tier);
            items.add(stack);
        }
    }

}
