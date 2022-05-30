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

import com.buuz135.industrial.fluid.OreTitaniumFluidAttributes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ItemStackUtils {

    public static ResourceLocation getOreTag(ItemStack stack) {
        Item item = stack.getItem();
        for (ResourceLocation resourceLocation : ForgeRegistries.ITEMS.tags().getReverseTag(stack.getItem()).map(IReverseTag::getTagKeys).map(tagKeyStream -> tagKeyStream.map(TagKey::location).collect(Collectors.toList())).orElse(new ArrayList<>())) {
            if (resourceLocation.toString().startsWith("minecraft:raw_materials/")){
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
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();// && !stack.getItem().equals(ForgeModContainer.getInstance().universalBucket);
    }


    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack) {
        return ColorUtils.getColorFrom(Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0).getParticleIcon());
    }

}
