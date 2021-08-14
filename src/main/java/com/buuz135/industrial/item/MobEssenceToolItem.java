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

package com.buuz135.industrial.item;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class MobEssenceToolItem extends IFCustomItem {

    public MobEssenceToolItem(ItemGroup group) {
        super("mob_essence_tool", group, new Properties().maxStackSize(1));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            addNBT(stack);
            items.add(stack);
        }
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        addNBT(stack);
    }

    private void addNBT(ItemStack stack) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Kills", 0);
        stack.setTag(compoundNBT);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (stack.hasTag() && stack.getTag().getInt("Kills") == 0) {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString("Entity", target.getType().getRegistryName().toString());
            compoundNBT.putInt("Kills", 1);
            stack.setTag(compoundNBT);
            playerIn.setHeldItem(hand, stack);
            target.remove(true);
            return ActionResultType.SUCCESS;
        }
        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }
}
