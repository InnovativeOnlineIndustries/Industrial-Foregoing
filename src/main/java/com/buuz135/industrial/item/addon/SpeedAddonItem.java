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
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

public class SpeedAddonItem extends IFCustomItem {

    private int tier;

    public SpeedAddonItem(int tier, CreativeModeTab group) {
        super("speed_addon_" + tier, group, new Properties().stacksTo(16));
        this.tier = tier;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        AugmentWrapper.setType(stack, AugmentTypes.SPEED, 1 + tier);
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TagKey<Item> tierMaterial = tier == 1 ? IndustrialTags.Items.GEAR_GOLD : IndustrialTags.Items.GEAR_DIAMOND;
        new DissolutionChamberRecipe(getRegistryName(), new Ingredient.Value[]{
                new Ingredient.ItemValue(new ItemStack(Items.REDSTONE)),
                new Ingredient.ItemValue(new ItemStack(Items.REDSTONE)),
                new Ingredient.ItemValue(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.ItemValue(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.TagValue(tierMaterial),
                new Ingredient.TagValue(tierMaterial),
                new Ingredient.ItemValue(new ItemStack(Items.SUGAR)),
                new Ingredient.ItemValue(new ItemStack(Items.SUGAR))
        }, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 1000), 200, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public String getDescriptionId() {
        return new TranslatableComponent("item.industrialforegoing.addon").getString() + new TranslatableComponent("item.industrialforegoing.speed").getString() + "Tier " + tier + " ";
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            AugmentWrapper.setType(stack, AugmentTypes.SPEED, 1 + tier);
            items.add(stack);
        }
    }

}
