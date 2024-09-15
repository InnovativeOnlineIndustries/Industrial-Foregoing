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

package com.buuz135.industrial.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemStackUtils {

    public static ResourceLocation getOreTag(ItemStack stack) {
        Item item = stack.getItem();
        for (ResourceLocation resourceLocation : stack.getTags().map(TagKey::location).toList()) {
            if (resourceLocation.toString().startsWith("c:raw_materials/")) {
                return resourceLocation;
            }
        }
        return null;
    }

    public static boolean isInventoryFull(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    public static boolean isChorusFlower(ItemStack stack) {
        return !Block.byItem(stack.getItem()).equals(Blocks.AIR) && Block.byItem(stack.getItem()).equals(Blocks.CHORUS_FLOWER);
    }

    public static boolean acceptsFluidItem(ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;// && !stack.getItem().equals(ForgeModContainer.getInstance().universalBucket);
    }


    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack) {
        return ColorUtils.getColorFrom(Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0).getParticleIcon());
    }

}
