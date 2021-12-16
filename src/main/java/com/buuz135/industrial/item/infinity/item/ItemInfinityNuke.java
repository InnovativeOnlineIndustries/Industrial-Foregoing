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
package com.buuz135.industrial.item.infinity.item;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.entity.InfinityNukeEntity;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class ItemInfinityNuke extends ItemInfinity {

    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityNuke(CreativeModeTab group) {
        super("infinity_nuke", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, true);
    }

    public static int getRadius(ItemStack stack) {
        int tier = getSelectedTier(stack).getRadius() + 1;
        double fluidAmount = 1 + ((ItemInfinityNuke)ModuleTool.INFINITY_NUKE.get()).getFuelFromStack(stack) / 1_000_000D * 0.5;
        return (int) ((1 + Math.ceil((tier * tier * tier) / 2D)) * fluidAmount);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InfinityNukeEntity entity = new InfinityNukeEntity(context.getLevel(), context.getPlayer(), context.getItemInHand().copy());
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        context.getPlayer().setItemInHand(context.getHand(), ItemStack.EMPTY);
        context.getLevel().addFreshEntity(entity);
        IndustrialForegoing.LOGGER.info(context.getPlayer().getUUID() + " (" + context.getPlayer().getDisplayName().toString() + ") placed an Infinity Nuke");
        return InteractionResult.SUCCESS;
    }

    @Override
    public String getFormattedArea(ItemStack stack, InfinityTier tier, int radius, boolean usesDepth) {
        return getRadius(stack) + "r";
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Items.TNT)),
                        new Ingredient.ItemValue(new ItemStack(Items.TNT)),
                        new Ingredient.ItemValue(new ItemStack(Items.TNT)),
                        new Ingredient.ItemValue(new ItemStack(Items.TNT)),
                        new Ingredient.ItemValue(new ItemStack(ModuleCore.RANGE_ADDONS[11].get())),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_STAR)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_STAR)),
                },
                new FluidStack(ModuleCore.ETHER.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }
}
