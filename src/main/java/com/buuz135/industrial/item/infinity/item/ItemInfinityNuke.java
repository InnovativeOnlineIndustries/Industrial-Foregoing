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
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class ItemInfinityNuke extends ItemInfinity {

    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityNuke(ItemGroup group) {
        super("infinity_nuke", group, new Properties().maxStackSize(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, true);
    }

    public static int getRadius(ItemStack stack) {
        int tier = getSelectedTier(stack).getRadius() + 1;
        double fluidAmount = 1 + ModuleTool.INFINITY_NUKE.getFuelFromStack(stack) / 1_000_000D * 0.5;
        return (int) ((1 + Math.ceil((tier * tier * tier) / 2D)) * fluidAmount);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        InfinityNukeEntity entity = new InfinityNukeEntity(context.getWorld(), context.getPlayer(), context.getItem().copy());
        BlockPos blockPos = context.getPos().offset(context.getFace());
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        context.getPlayer().setHeldItem(context.getHand(), ItemStack.EMPTY);
        context.getWorld().addEntity(entity);
        IndustrialForegoing.LOGGER.info(context.getPlayer().getUniqueID() + " (" + context.getPlayer().getDisplayName().toString() + ") placed an Infinity Nuke");
        return ActionResultType.SUCCESS;
    }

    @Override
    public String getFormattedArea(ItemStack stack, InfinityTier tier, int radius, boolean usesDepth) {
        return getRadius(stack) + "r";
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(Items.TNT)),
                        new Ingredient.SingleItemList(new ItemStack(Items.TNT)),
                        new Ingredient.SingleItemList(new ItemStack(Items.TNT)),
                        new Ingredient.SingleItemList(new ItemStack(Items.TNT)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.NETHER_STAR)),
                        new Ingredient.SingleItemList(new ItemStack(Items.NETHER_STAR)),
                },
                new FluidStack(ModuleCore.ETHER.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }
}
