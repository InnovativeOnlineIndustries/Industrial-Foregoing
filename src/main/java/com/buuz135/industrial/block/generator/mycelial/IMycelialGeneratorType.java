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
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface IMycelialGeneratorType {

    public static List<IMycelialGeneratorType> TYPES = Arrays.asList(new FurnaceGeneratorType(), new SlimeyGeneratorType(), new CulinaryGeneratorType(), new PotionGeneratorType(), new DisenchantmentGeneratorType(),
            new EnderGeneratorType(), new ExplosiveGeneratorType(), new FrostyGeneratorType(), new HalitosisGeneratorType(), new MagmaGeneratorType(), new PinkGeneratorType(), new NetherStarGeneratorType(), new DeathGeneratorType(),
            new FireworkGeneratorType(), new CrimedGeneratorType(), new MeatallurgicGeneratorType());

    String getName();

    Input[] getInputs();

    List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates();

    List<Predicate<FluidStack>> getTankInputPredicates();

    boolean canStart(INBTSerializable<CompoundTag>[] inputs);

    Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundTag>[] inputs);

    DyeColor[] getInputColors();

    Item getDisplay();

    int getSlotSize();

    List<MycelialGeneratorRecipe> getRecipes(RegistryAccess registryAccess);

    ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder);

    default void onTick(Level world, BlockPos pos) {

    }

    public enum Input {
        SLOT,
        TANK;
    }
}
