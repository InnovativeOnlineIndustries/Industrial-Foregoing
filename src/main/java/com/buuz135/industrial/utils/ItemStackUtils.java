/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemStackUtils {

    @SideOnly(Side.CLIENT)
    public static void renderItemIntoGUI(ItemStack stack, int x, int y, int gl) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        IBakedModel bakedmodel = renderItem.getItemModelWithOverrides(stack, null, null);
        GlStateManager.translate((float) x, (float) y, 100.0F + renderItem.zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);
        if (bakedmodel.isGui3d()) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            if (bakedmodel.isBuiltInRenderer()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                TileEntityItemStackRenderer.instance.renderByItem(stack);
            } else {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder vertexbuffer = tessellator.getBuffer();
                vertexbuffer.begin(gl, DefaultVertexFormats.ITEM);
                for (EnumFacing enumfacing : EnumFacing.values()) {
                    renderQuads(vertexbuffer, bakedmodel.getQuads(null, enumfacing, 0L), -1, stack);
                }
                renderQuads(vertexbuffer, bakedmodel.getQuads(null, null, 0L), -1, stack);
                tessellator.draw();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }


    private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = (BakedQuad) quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex()) {
                k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

    public static void renderEntity(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        //GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        // GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        //       ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static boolean isStackOreDict(ItemStack stack, String string) {
        if (stack.isEmpty()) return false;
        int id = OreDictionary.getOreID(string);
        for (int i : OreDictionary.getOreIDs(stack)) {
            if (i == id) return true;
        }
        return false;
    }

    public static boolean isOre(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int i : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(i).startsWith("ore")) return true;
        }
        return false;
    }

    public static boolean isInventoryFull(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    public static List<String> getOreDictionaryEntries(ItemStack stack) {
        List<String> dict = new ArrayList<>();
        for (int i : OreDictionary.getOreIDs(stack)) {
            dict.add(OreDictionary.getOreName(i));
        }
        return dict;
    }

    public static List<ItemStack> getOreItems(String ore) {
        return OreDictionary.getOres(ore);
    }

    public static boolean isChorusFlower(ItemStack stack) {
        return !Block.getBlockFromItem(stack.getItem()).equals(Blocks.AIR) && Block.getBlockFromItem(stack.getItem()).equals(Blocks.CHORUS_FLOWER);
    }

    public static boolean acceptsFluidItem(ItemStack stack) {
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) && !stack.getItem().equals(ForgeModContainer.getInstance().universalBucket);
    }

    @SideOnly(Side.CLIENT)
    public static int getColor(ItemStack stack) {
        return ColorUtils.getColorFrom(Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, Minecraft.getMinecraft().world, Minecraft.getMinecraft().player).getParticleTexture(), Color.GRAY);
    }

    public static void fillItemFromTank(ItemStackHandler fluidItems, IFluidTank tank) {
        if (tank.getFluid() == null)
            return;
        ItemStack stack = fluidItems.getStackInSlot(0).copy();
        if (!stack.isEmpty()) {
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                FluidActionResult result = FluidUtil.tryFillContainer(stack, (IFluidHandler) tank, tank.getCapacity(), null, false);
                if (result.isSuccess() && (fluidItems.getStackInSlot(1).isEmpty() ||
                        (ItemHandlerHelper.canItemStacksStack(result.getResult(), fluidItems.getStackInSlot(1)) && result.getResult().getCount() + fluidItems.getStackInSlot(1).getCount() <= result.getResult().getMaxStackSize()))) {
                    result = FluidUtil.tryFillContainer(stack, (IFluidHandler) tank, tank.getCapacity(), null, true);
                    if (fluidItems.getStackInSlot(1).isEmpty()) {
                        fluidItems.setStackInSlot(1, result.getResult());
                    } else {
                        fluidItems.getStackInSlot(1).grow(1);
                    }
                    fluidItems.getStackInSlot(0).shrink(1);
                }
            }
        }
    }

    public static void fillTankFromItem(ItemStackHandler fluidItems, IFluidTank tank) {
        ItemStack stack = fluidItems.getStackInSlot(0).copy();
        if (!stack.isEmpty()) {
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                FluidStack fluidStack = cap.drain(1000, false);
                if (fluidStack != null && tank.fill(fluidStack, false) == 0) return;
                FluidActionResult result = FluidUtil.tryEmptyContainer(stack, (IFluidHandler) tank, 1000, null, false);
                if (result.isSuccess() && (fluidItems.getStackInSlot(1).isEmpty() ||
                        (ItemHandlerHelper.canItemStacksStack(result.getResult(), fluidItems.getStackInSlot(1)) && result.getResult().getCount() + fluidItems.getStackInSlot(1).getCount() <= result.getResult().getMaxStackSize()))) {
                    result = FluidUtil.tryEmptyContainer(stack, (IFluidHandler) tank, tank.getCapacity(), null, true);
                    if (fluidItems.getStackInSlot(1).isEmpty()) {
                        fluidItems.setStackInSlot(1, result.getResult());
                    } else {
                        fluidItems.getStackInSlot(1).grow(1);
                    }
                    fluidItems.getStackInSlot(0).shrink(1);
                }
            }
        }
    }

}
