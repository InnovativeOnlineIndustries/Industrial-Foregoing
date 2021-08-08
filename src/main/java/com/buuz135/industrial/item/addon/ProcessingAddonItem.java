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
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class ProcessingAddonItem extends IFCustomItem {

    public static final IAugmentType PROCESSING = () -> "Processing";

    private int tier;

    public ProcessingAddonItem(int tier, ItemGroup group) {
        super("processing_addon_" + tier, group, new Properties().maxStackSize(16));
        this.tier = tier;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        AugmentWrapper.setType(stack, PROCESSING, 1 + tier);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        ITag<Item> tierMaterial = tier == 1 ? IndustrialTags.Items.GEAR_GOLD : IndustrialTags.Items.GEAR_DIAMOND;
        new DissolutionChamberRecipe(getRegistryName(), new Ingredient.IItemList[]{
                new Ingredient.SingleItemList(new ItemStack(Items.REDSTONE)),
                new Ingredient.SingleItemList(new ItemStack(Items.REDSTONE)),
                new Ingredient.SingleItemList(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.SingleItemList(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.TagList(tierMaterial),
                new Ingredient.TagList(tierMaterial),
                new Ingredient.SingleItemList(new ItemStack(Items.FURNACE)),
                new Ingredient.SingleItemList(new ItemStack(Items.CRAFTING_TABLE))
        }, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 1000), 200, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public String getTranslationKey() {
        return new TranslationTextComponent("item.industrialforegoing.addon").getString() + new TranslationTextComponent("item.industrialforegoing.processing").getString() + "Tier " + tier + " ";
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            AugmentWrapper.setType(stack, PROCESSING, 1 + tier);
            items.add(stack);
        }
    }
}
