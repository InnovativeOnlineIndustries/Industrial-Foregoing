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

import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.fluid.ClientFluidTypeExtensions;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class OreTitaniumFluidType extends FluidType {

    public static final String NBT_TAG = "Tag";

    public OreTitaniumFluidType(final Properties properties) {
        super(properties);
    }

    @Override
    public Component getDescription(FluidStack stack) {
        String extra = "";
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = TagUtil.getAllEntries(ForgeRegistries.ITEMS, TagUtil.getItemTag(new ResourceLocation(tag.replace("forge:raw_materials/", "forge:dusts/")))).stream().toList();
            if (items.size() > 0) {
                extra = " (" + Component.translatable(items.get(0).getDescriptionId()).getString() + ")";
            }
        }
        return Component.literal(super.getDescription(stack).getString() + extra);
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        String extra = "";
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = TagUtil.getAllEntries(ForgeRegistries.ITEMS, TagUtil.getItemTag(new ResourceLocation(tag.replace("forge:raw_materials/", "forge:dusts/")))).stream().toList();
            if (items.size() > 0) {
                extra = " (" + Component.translatable(items.get(0).getDescriptionId()).getString() + ")";
            }
        }
        return Component.translatable(super.getDescriptionId(stack)).getString() + extra;
    }


    @Override
    public ItemStack getBucket(FluidStack stack) {
        ItemStack bucket = super.getBucket(stack);
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
            String tag = stack.getTag().getString(NBT_TAG);
            bucket.getOrCreateTag().putString(NBT_TAG, tag);
        }
        return bucket;
    }

    public static FluidStack getFluidWithTag(OreFluidInstance fluidInstance, int amount, ResourceLocation itemITag) {
        FluidStack stack = new FluidStack(fluidInstance.getSourceFluid(), amount);
        stack.getOrCreateTag().putString(NBT_TAG, itemITag.toString());
        return stack;
    }

    public static String getFluidTag(FluidStack stack) {
        return stack.getOrCreateTag().getString(NBT_TAG);
    }

    public static boolean isValid(ResourceLocation resourceLocation) {
        TagKey<Item> key = TagUtil.getItemTag(new ResourceLocation("forge:dusts/" + resourceLocation.toString().replace("forge:raw_materials/", "")));
        return ForgeRegistries.ITEMS.tags().isKnownTagName(key) && !TagUtil.getAllEntries(ForgeRegistries.ITEMS, key).isEmpty();
    }

    public static ItemStack getOutputDust(FluidStack stack) {
        String tag = getFluidTag(stack);
        return TagUtil.getItemWithPreference(TagUtil.getItemTag(new ResourceLocation(tag.replace("forge:raw_materials/", "forge:dusts/"))));
    }


    public static class Client extends ClientFluidTypeExtensions {

        public Client(ResourceLocation still, ResourceLocation flow) {
            super(still, flow);
        }

        @Override
        public int getTintColor(FluidStack stack) {
            if (Minecraft.getInstance().level != null && stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
                String tag = stack.getTag().getString(NBT_TAG);
                List<Item> items = TagUtil.getAllEntries(ForgeRegistries.ITEMS, TagUtil.getItemTag(new ResourceLocation(tag.replace("forge:raw_materials/", "forge:dusts/")))).stream().toList();
                if (items.size() > 0) {
                    return ItemStackUtils.getColor(new ItemStack(items.get(0)));
                }
            }
            return 0xFFFFFFFF;
        }

    }
}
