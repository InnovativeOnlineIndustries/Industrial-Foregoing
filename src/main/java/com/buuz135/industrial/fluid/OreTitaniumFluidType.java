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

import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.fluid.ClientFluidTypeExtensions;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.HashMap;
import java.util.List;

public class OreTitaniumFluidType extends FluidType {

    public OreTitaniumFluidType(final Properties properties) {
        super(properties);
    }

    public static HashMap<String, TagKey<Item>> TAG_CACHE = new HashMap<>();

    public static FluidStack getFluidWithTag(OreFluidInstance fluidInstance, int amount, ResourceLocation itemITag) {
        FluidStack stack = new FluidStack(fluidInstance.getSourceFluid(), amount);
        stack.set(IFAttachments.ORE_FLUID_TAG, itemITag.toString());
        return stack;
    }

    public static String getFluidTag(FluidStack stack) {
        return stack.getOrDefault(IFAttachments.ORE_FLUID_TAG, "");
    }

    public static boolean isValid(ResourceLocation resourceLocation) {
        TagKey<Item> key = TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "dusts/" + resourceLocation.toString().replace("c:raw_materials/", "")));
        return BuiltInRegistries.ITEM.getTag(key).isPresent() && !TagUtil.getAllEntries(BuiltInRegistries.ITEM, key).isEmpty();
    }

    public static ItemStack getOutputDust(FluidStack stack) {
        String tag = getFluidTag(stack);
        if (TAG_CACHE.containsKey(tag)) {
            return TagUtil.getItemWithPreference(TAG_CACHE.get(tag));
        }
        var itemTag = TagUtil.getItemTag(ResourceLocation.parse(tag.replace("c:raw_materials/", "c:dusts/")));
        TAG_CACHE.put(tag, itemTag);
        return TagUtil.getItemWithPreference(itemTag);
    }

    @Override
    public Component getDescription(FluidStack stack) {
        return super.getDescription(stack);
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        String extra = "";
        if (stack.has(IFAttachments.ORE_FLUID_TAG)) {
            String tag = stack.get(IFAttachments.ORE_FLUID_TAG);
            List<Item> items = TagUtil.getAllEntries(BuiltInRegistries.ITEM, TagUtil.getItemTag(ResourceLocation.parse(tag.replace("c:raw_materials/", "c:dusts/")))).stream().toList();
            if (items.size() > 0) {
                extra = " (" + Component.translatable(items.get(0).getDescriptionId()).getString() + ")";
            }
        }
        return Component.translatable(super.getDescriptionId(stack)).getString() + extra;
    }

    @Override
    public ItemStack getBucket(FluidStack stack) {
        ItemStack bucket = super.getBucket(stack);
        if (stack.has(IFAttachments.ORE_FLUID_TAG)) {
            String tag = stack.get(IFAttachments.ORE_FLUID_TAG);
            bucket.set(IFAttachments.ORE_FLUID_TAG, tag);
        }
        return bucket;
    }

    public static class Client extends ClientFluidTypeExtensions {

        public Client(ResourceLocation still, ResourceLocation flow) {
            super(still, flow);
        }

        @Override
        public int getTintColor(FluidStack stack) {
            if (Minecraft.getInstance().level != null && stack.has(IFAttachments.ORE_FLUID_TAG)) {
                String tag = stack.get(IFAttachments.ORE_FLUID_TAG);
                List<Item> items = TagUtil.getAllEntries(BuiltInRegistries.ITEM, TagUtil.getItemTag(ResourceLocation.parse(tag.replace("c:raw_materials/", "c:dusts/")))).stream().toList();
                if (items.size() > 0) {
                    return ItemStackUtils.getColor(new ItemStack(items.get(0)));
                }
            }
            return 0xFFFFFFFF;
        }

    }
}
