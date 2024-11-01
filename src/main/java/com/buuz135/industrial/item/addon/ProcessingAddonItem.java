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

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.ISpecialCreativeTabItem;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


public class ProcessingAddonItem extends AddonItem implements ISpecialCreativeTabItem {

    public static final IAugmentType PROCESSING = () -> "Processing";

    private int tier;

    public ProcessingAddonItem(int tier, TitaniumTab group) {
        super("processing_addon_tier_" + tier, group, new Properties().stacksTo(16));
        this.tier = tier;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        AugmentWrapper.setType(stack, PROCESSING, 1 + tier);
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TagKey<Item> tierMaterial = tier == 1 ? IndustrialTags.Items.GEAR_GOLD : IndustrialTags.Items.GEAR_DIAMOND;
        DissolutionChamberRecipe.createRecipe(consumer, "processing_addon_tier_" + tier, new DissolutionChamberRecipe(List.of(
                Ingredient.of(new ItemStack(Items.REDSTONE)),
                Ingredient.of(new ItemStack(Items.REDSTONE)),
                Ingredient.of(new ItemStack(Items.GLASS_PANE)),
                Ingredient.of(new ItemStack(Items.GLASS_PANE)),
                Ingredient.of(tierMaterial),
                Ingredient.of(tierMaterial),
                Ingredient.of(new ItemStack(Items.FURNACE)),
                Ingredient.of(new ItemStack(Items.CRAFTING_TABLE))
        ), new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 1000), 200, Optional.of(new ItemStack(this)), Optional.empty()));

    }

    @Override
    public String getDescriptionId() {
        return Component.translatable("item.industrialforegoing.addon").getString() + Component.translatable("item.industrialforegoing.processing").getString() + "Tier " + tier + " ";
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        tooltip.add(Component.literal(ChatFormatting.GRAY + Component.translatable("text.industrialforegoing.tooltip.processing").getString() + "x" + (tier + 1) * 1.0));
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addToTab(BuildCreativeModeTabContentsEvent event) {
        var stack = new ItemStack(this);
        AugmentWrapper.setType(stack, PROCESSING, 1 + tier);
        event.accept(stack);
    }
}
