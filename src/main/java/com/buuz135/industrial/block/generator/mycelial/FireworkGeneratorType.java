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
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FireworkGeneratorType implements IMycelialGeneratorType{


    @Override
    public String getName() {
        return "rocket";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, slot) -> stack.getItem() instanceof FireworkRocketItem && stack.hasTag());
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return new ArrayList<>();
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundNBT>[] inputs) {
        return inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0;
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs) {
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0){
            ItemStack stack = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0);
            stack.shrink(1);
            return calculate(stack);
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.RED};
    }

    @Override
    public Item getDisplay() {
        return Items.FIREWORK_ROCKET;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        return new ArrayList<>();
    }

    public static FireworkRocketItem.Shape get(int indexIn) {
        return indexIn >= 0 && indexIn < FireworkRocketItem.Shape.values().length ? FireworkRocketItem.Shape.values()[indexIn] : FireworkRocketItem.Shape.SMALL_BALL;
    }

    private Pair<Integer, Integer> calculate(ItemStack stack) {
        CompoundNBT nbt = stack.getChildTag("Fireworks");
        int flight = nbt.getInt("Flight");
        double power = 1;
        ListNBT listnbt = nbt.getList("Explosions", 10);
        if (!listnbt.isEmpty()) {
            for (int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compound = listnbt.getCompound(i);
                FireworkRocketItem.Shape shape = get(compound.getByte("Type"));
                power *= getShapeModifier(shape);
                int[] colors = compound.getIntArray("Colors");
                power *= (1 + colors.length / 100D);
                int[] fadeColors = compound.getIntArray("FadeColors");
                power *= (1 + fadeColors.length / 90D);
                if (compound.getBoolean("Trail")) {
                    power *= 1.6;
                }
                if (compound.getBoolean("Flicker")) {
                    power *= 1.4;
                }
            }
        }
        return Pair.of((int)(80 * power),  60 * flight);
    }

    private double getShapeModifier(FireworkRocketItem.Shape shape){
        switch (shape){
            case STAR: return 1.2;
            case BURST: return 1.05;
            case CREEPER: return 1.5;
            case LARGE_BALL: return 1.1;
            default: return 1.01;
        }
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Items.GUNPOWDER)
                .key('C', Items.PAPER)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }

    @Override
    public void onTick(World world, BlockPos pos) {
        AxisAlignedBB area = new AxisAlignedBB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3);
        for (LivingEntity livingEntity : world.getEntitiesWithinAABB(LivingEntity.class, area)) {
            livingEntity.addPotionEffect(new EffectInstance(Effects.LEVITATION, 10, 2));
        }
    }
}

