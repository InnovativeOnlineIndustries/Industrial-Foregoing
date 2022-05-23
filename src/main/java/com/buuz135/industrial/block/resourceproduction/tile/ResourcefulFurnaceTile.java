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
package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.ResourcefulFurnaceConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ResourcefulFurnaceTile extends IndustrialProcessingTile<ResourcefulFurnaceTile> {

    private int getPowerPerTick;

    @Save
    private SidedInventoryComponent<ResourcefulFurnaceTile> input;
    @Save
    private SidedInventoryComponent<ResourcefulFurnaceTile> output;
    @Save
    private SidedFluidTankComponent<ResourcefulFurnaceTile> tank;

    private SmeltingRecipe[] recipes;

    public ResourcefulFurnaceTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.RESOURCEFUL_FURNACE, 74, 22 + 18, blockPos, blockState);
        addInventory(this.input = (SidedInventoryComponent<ResourcefulFurnaceTile>) new SidedInventoryComponent<ResourcefulFurnaceTile>("input", 44, 22, 3, 0).
                setColor(DyeColor.BLUE).
                setSlotLimit(1).
                setRange(1, 3).
                setOnSlotChanged((itemStack, integer) -> {
                    checkForRecipe(integer);
                }));
        addInventory(this.output = (SidedInventoryComponent<ResourcefulFurnaceTile>) new SidedInventoryComponent<ResourcefulFurnaceTile>("output", 110, 22, 3, 1).
                setColor(DyeColor.ORANGE).
                setInputFilter((itemStack, integer) -> false).
                setRange(1, 3));
        addTank(this.tank = (SidedFluidTankComponent<ResourcefulFurnaceTile>) new SidedFluidTankComponent<ResourcefulFurnaceTile>("essence", ResourcefulFurnaceConfig.maxEssenceTankSize, 132, 20, 2).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.DRAIN));
        this.recipes = new SmeltingRecipe[3];
        this.getPowerPerTick = ResourcefulFurnaceConfig.powerPerTick;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        checkForRecipe(0);
        checkForRecipe(1);
        checkForRecipe(2);
    }

    private void checkForRecipe(int slot) {
        Collection<SmeltingRecipe> recipes = RecipeUtil.getCookingRecipes(this.level);
        this.recipes[slot] = recipes.stream().filter(furnaceRecipe -> furnaceRecipe.getIngredients().get(0).test(input.getStackInSlot(slot))).findAny().orElse(null);
    }

    @Override
    public boolean canIncrease() {
        for (SmeltingRecipe recipe : this.recipes) {
            if (recipe != null && ItemHandlerHelper.insertItem(output, recipe.getResultItem().copy(), true).isEmpty())
                return true;
        }
        return false;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            for (int i = 0; i < this.recipes.length; i++) {
                SmeltingRecipe recipe = this.recipes[i];
                if (recipe != null && ItemHandlerHelper.insertItem(output, recipe.getResultItem().copy(), true).isEmpty()) {
                    if (ItemHandlerHelper.insertItem(output, recipe.getResultItem().copy(), true).isEmpty()) {
                        input.setStackInSlot(i, ItemStack.EMPTY);
                        ItemHandlerHelper.insertItem(output, recipe.getResultItem().copy(), false);
                        tank.fillForced(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), (int) (recipe.getExperience() * 20)), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        };
    }

    @Override
    protected EnergyStorageComponent<ResourcefulFurnaceTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(ResourcefulFurnaceConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return getPowerPerTick;
    }


    @Nonnull
    @Override
    public ResourcefulFurnaceTile getSelf() {
        return this;
    }
}
