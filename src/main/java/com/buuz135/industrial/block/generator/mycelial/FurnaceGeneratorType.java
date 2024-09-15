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

package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FurnaceGeneratorType implements IMycelialGeneratorType {

    @Override
    public String getName() {
        return "furnace";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Collections.singletonList((stack, slot) -> stack.getBurnTime(RecipeType.SMELTING) > 0);
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return new ArrayList<>();
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundTag>[] inputs) {
        return inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0;
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundTag>[] inputs) {
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0) {
            ItemStack itemstack = ((SidedInventoryComponent) inputs[0]).getStackInSlot(0);
            int burnTime = itemstack.getBurnTime(RecipeType.SMELTING);
            if (itemstack.hasCraftingRemainingItem())
                ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, itemstack.getCraftingRemainingItem());
            else if (!itemstack.isEmpty()) {
                itemstack.shrink(1);
                if (itemstack.isEmpty()) {
                    ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, itemstack.getCraftingRemainingItem());
                }
            }
            return Pair.of(burnTime, 80);
        }
        return Pair.of(0, 80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.BLUE};
    }

    @Override
    public Item getDisplay() {
        return Items.FURNACE;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes(RegistryAccess registryAccess) {
        return BuiltInRegistries.ITEM.stream().map(ItemStack::new).filter(stack -> stack.getBurnTime(RecipeType.SMELTING) > 0).map(item -> new MycelialGeneratorRecipe(Collections.singletonList(Collections.singletonList(Ingredient.of(item))), new ArrayList<>(), item.getBurnTime(RecipeType.SMELTING), 80)).collect(Collectors.toList());
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.define('B', Tags.Items.STORAGE_BLOCKS_COAL)
                .define('C', Blocks.FURNACE)
                .define('M', IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
        return recipeBuilder;
    }
}
