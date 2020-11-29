package com.buuz135.industrial.block.generator.mycelial;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface IMycelialGeneratorType {

    public static List<IMycelialGeneratorType> TYPES = Arrays.asList(new FurnaceGeneratorType(), new SlimeyGeneratorType(), new CulinaryGeneratorType(), new PotionGeneratorType(), new DisenchantmentGeneratorType());

    String getName();

    Input[] getInputs();

    List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates();

    List<Predicate<FluidStack>> getTankInputPredicates();

    boolean canStart(INBTSerializable<CompoundNBT>[] inputs);

    Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs);

    DyeColor[] getInputColors();

    Item getDisplay();

    public enum Input {
        SLOT,
        TANK;
    }
}
