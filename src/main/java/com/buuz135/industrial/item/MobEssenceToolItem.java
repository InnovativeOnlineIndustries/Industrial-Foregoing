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

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

public class MobEssenceToolItem extends IFCustomItem {

    public MobEssenceToolItem(CreativeModeTab group) {
        super("mob_essence_tool", group, new Properties().stacksTo(1));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            addNBT(stack);
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        addNBT(stack);
    }

    private void addNBT(ItemStack stack) {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("Kills", 0);
        stack.setTag(compoundNBT);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        if (stack.hasTag() && stack.getTag().getInt("Kills") == 0) {
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.putString("Entity", target.getType().getRegistryName().toString());
            compoundNBT.putInt("Kills", 1);
            stack.setTag(compoundNBT);
            playerIn.setItemInHand(hand, stack);
            target.remove(true);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {

    }
}
