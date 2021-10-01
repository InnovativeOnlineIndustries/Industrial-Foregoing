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
package com.buuz135.industrial.fluid;

import java.util.List;

import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class OreTitaniumFluidAttributes extends FluidAttributes {

    public static final String NBT_TAG = "Tag";

    public OreTitaniumFluidAttributes(Builder builder, Fluid fluid) {
        super(builder, fluid);
    }

    @Override
    public int getColor(FluidStack stack) {
        if (Minecraft.getInstance().level != null && stack.hasTag() && stack.getTag().contains(NBT_TAG)){
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, new ResourceLocation(tag.replace("forge:ores/", "forge:dusts/")), resourceLocation -> null).getValues();
            if (items.size() > 0){
//                return ItemStackUtils.getColor(new ItemStack(items.get(0)));
                return 0;
            }
        }
        return super.getColor(stack);
    }

    @Override
    public String getTranslationKey(FluidStack stack) {
        String extra = "";
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)){
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY,new ResourceLocation(tag), resourceLocation -> null).getValues();
            if (items.size() > 0){
                extra = " (" + new TranslatableComponent(items.get(0).getDescriptionId()).getString() + ")";
            }
        }
        return new TranslatableComponent(super.getTranslationKey(stack)).getString() + extra;
    }

    @Override
    public Component getDisplayName(FluidStack stack) {
        String extra = "";
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)){
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY,new ResourceLocation(tag), resourceLocation -> null).getValues();
            if (items.size() > 0){
                extra = " (" + new TranslatableComponent(items.get(0).getDescriptionId()).getString() + ")";
            }
        }
        return new TextComponent(super.getDisplayName(stack).getString() + extra);
    }

    public static FluidStack getFluidWithTag(OreFluidInstance fluidInstance, int amount, ResourceLocation itemITag){
        FluidStack stack = new FluidStack(fluidInstance.getSourceFluid(), amount);
        stack.getOrCreateTag().putString(NBT_TAG, itemITag.toString());
        return stack;
    }

    public static String getFluidTag(FluidStack stack){
        return stack.getOrCreateTag().getString(NBT_TAG);
    }

    public static boolean isValid(ResourceLocation resourceLocation){
        // TODO: 26/07/2021 Fix
//        return SerializationTags.getInstance().getItems().getAvailableTags().contains(new ResourceLocation("forge:dusts/" + resourceLocation.toString().replace("forge:ores/", ""))) && !SerializationTags.getInstance().getItems().getTag(resourceLocation).getValues().isEmpty();
        return false;
    }


    public static ItemStack getOutputDust(FluidStack stack){
        String tag = getFluidTag(stack);
        // TODO: 26/07/2021 fix
        return ItemStack.EMPTY;
//        return TagUtil.getItemWithPreference(SerializationTags.getInstance().getItems().getTag(new ResourceLocation(tag.replace("forge:ores/", "forge:dusts/"))));
    }

    @Override
    public ItemStack getBucket(FluidStack stack) {
        ItemStack bucket = super.getBucket(stack);
        if(stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
            String tag = stack.getTag().getString(NBT_TAG);
            bucket.getOrCreateTag().putString(NBT_TAG, tag);
        }
        return bucket;
    }
}
