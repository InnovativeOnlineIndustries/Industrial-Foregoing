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
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
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

    boolean canStart(INBTSerializable<CompoundNBT>[] inputs);

    Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs);

    DyeColor[] getInputColors();

    Item getDisplay();

    int getSlotSize();

    List<MycelialGeneratorRecipe> getRecipes();

    ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder);

    default void onTick(World world, BlockPos pos){

    }

    public enum Input {
        SLOT,
        TANK;
    }
}
