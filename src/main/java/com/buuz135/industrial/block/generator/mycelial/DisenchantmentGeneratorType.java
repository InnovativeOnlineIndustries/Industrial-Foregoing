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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DisenchantmentGeneratorType implements IMycelialGeneratorType{

    @Override
    public String getName() {
        return "disenchantment";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT, Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, integer) -> stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem, (stack, integer) -> false);
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return new ArrayList<>();
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundTag>[] inputs) {
        return inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && inputs[1] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0 && getSlotInputPredicates().get(0).test(((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0), 0) && ((SidedInventoryComponent<?>) inputs[1]).getStackInSlot(0).isEmpty();
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundTag>[] inputs) {
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0) {
            ItemStack itemstack = ((SidedInventoryComponent) inputs[0]).getStackInSlot(0).copy();
            ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, ItemStack.EMPTY);
            if (itemstack.getItem().equals(Items.ENCHANTED_BOOK)){
                ((SidedInventoryComponent) inputs[1]).setStackInSlot(0, new ItemStack(Items.BOOK));
            } else {
                ((SidedInventoryComponent) inputs[1]).setStackInSlot(0, new ItemStack(itemstack.getItem()));
            }
            return calculate(itemstack);
        }
        return Pair.of(0, 80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.LIME, DyeColor.ORANGE};
    }

    @Override
    public Item getDisplay() {
        return Items.ENCHANTED_BOOK;
    }

    @Override
    public int getSlotSize() {
        return 1;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        List<MycelialGeneratorRecipe> recipes = new ArrayList<>();
        for (Enchantment value : ForgeRegistries.ENCHANTMENTS.getValues()) {
            for (int i = value.getMinLevel(); i <= value.getMaxLevel(); i++) {
                HashMap<Enchantment, Integer> map = new HashMap<>();
                map.put(value, i);
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(map, book);
                Pair<Integer, Integer> power = calculate(book);
                recipes.add(new MycelialGeneratorRecipe(Arrays.asList(Collections.singletonList(Ingredient.of(book)),Collections.singletonList(Ingredient.of(ItemStack.EMPTY))), new ArrayList<>(), power.getLeft(), power.getRight()));
            }
        }
        return recipes;
    }

    private Pair<Integer,Integer> calculate(ItemStack stack){
        Map<Enchantment, Integer> ench =  EnchantmentHelper.getEnchantments(stack);
        int rarity = 0;
        double level = 0;
        for (Map.Entry<Enchantment, Integer> enchEntry : ench.entrySet()) {
            rarity += (14 - enchEntry.getKey().getRarity().getWeight());
            level += (enchEntry.getValue() / (double) enchEntry.getKey().getMaxLevel());
        }
        return Pair.of(rarity * 80, (int) (160 * level));
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.define('B', Items.BOOK)
                .define('C', Blocks.GRINDSTONE)
                .define('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }
}
