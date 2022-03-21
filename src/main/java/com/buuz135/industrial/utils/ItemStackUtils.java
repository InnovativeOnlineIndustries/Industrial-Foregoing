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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

import java.awt.*;

public class ItemStackUtils {

    public static ResourceLocation getOreTag(ItemStack stack) {
        Item item = stack.getItem();
        // TODO: 22/08/2021 ore tags
//        for (ResourceLocation owningTag : SerializationTags.getInstance().getMatchingTags(item)) {
//            if (owningTag.toString().startsWith("forge:ores/")){
//               return owningTag;
//            }
//        }
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

    // TODO: 22/08/2021 render
//    @OnlyIn(Dist.CLIENT)
//    public static int getColor(ItemStack stack) {
//        return ColorUtils.getColorFrom(Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, Minecraft.getInstance().player).getParticleIcon());
//    }
//
//    public static void renderItemIntoGUI(PoseStack matrixStack, ItemStack stack, int x, int y) {
//        renderItemModelIntoGUI(matrixStack, stack, x, y, Minecraft.getInstance().getItemRenderer().getModel(stack, (Level)null, (LivingEntity)null));
//    }

    @OnlyIn(Dist.CLIENT)
    public static void renderItemModelIntoGUI(PoseStack matrixstack, ItemStack stack, int x, int y, BakedModel bakedmodel) {
//        RenderSystem.pushMatrix();
//        Minecraft.getInstance().getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
//        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
//        RenderSystem.enableRescaleNormal();
//        RenderSystem.enableAlphaTest();
//        RenderSystem.defaultAlphaFunc();
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.translatef((float)x, (float)y, 100.0F + Minecraft.getInstance().getItemRenderer().blitOffset);
//        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
//        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
//        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
//        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
//        boolean flag = !bakedmodel.usesBlockLight();
//        if (flag) {
//            Lighting.setupForFlatItems();
//        }
//
//        Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
//        irendertypebuffer$impl.endBatch();
//        RenderSystem.enableDepthTest();
//        if (flag) {
//            Lighting.setupFor3DItems();
//        }
//
//        RenderSystem.disableAlphaTest();
//        RenderSystem.disableRescaleNormal();
//        RenderSystem.popMatrix();
    }

}
