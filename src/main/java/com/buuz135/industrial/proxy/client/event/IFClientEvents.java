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
package com.buuz135.industrial.proxy.client.event;

import com.buuz135.industrial.item.infinity.ItemInfinityDrill;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.raytrace.DistanceRayTraceResult;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import org.apache.commons.lang3.tuple.Pair;

public class IFClientEvents {

    @SubscribeEvent
    public void textureStich(TextureStitchEvent.Pre pre) {
        pre.getMap().registerSprite(Minecraft.getInstance().getResourceManager(), new ResourceLocation(Reference.MOD_ID, "blocks/catears"));
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_RAW.getStill());
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_RAW.getFlowing());
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_FERMENTED.getStill());
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_FERMENTED.getFlowing());
    }

    @SubscribeEvent
    public void blockOverlayEvent(DrawBlockHighlightEvent event) {
        RayTraceResult hit = event.getTarget();
        if (hit.type == RayTraceResult.Type.BLOCK && hit instanceof DistanceRayTraceResult) {
            BlockPos pos = event.getTarget().getBlockPos();
            event.setCanceled(true);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            EntityPlayer player = event.getPlayer();
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

            Minecraft.getInstance().renderGlobal.drawSelectionBoundingBox(((DistanceRayTraceResult) hit).getHitBox().getBoundingBox().offset(-x, -y, -z).offset(pos).grow(0.002),
                    0.0F, 0.0F, 0.0F, 0.4F
            );

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
        if (hit.type == RayTraceResult.Type.BLOCK && event.getPlayer().getHeldItemMainhand().getItem().equals(ModuleTool.INFINITY_DRILL)) {
            event.setCanceled(true);
            ItemStack hand = event.getPlayer().getHeldItemMainhand();
            ItemInfinityDrill.DrillTier tier = ModuleTool.INFINITY_DRILL.getSelectedDrillTier(hand);
            World world = event.getPlayer().world;
            Pair<BlockPos, BlockPos> area = ModuleTool.INFINITY_DRILL.getArea(event.getTarget().getBlockPos(), event.getTarget().sideHit, tier, false);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            EntityPlayer player = event.getPlayer();
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
            BlockPos.getAllInBox(area.getLeft(), area.getRight()).forEach(blockPos -> {
                if (!world.isAirBlock(blockPos) && world.getBlockState(blockPos).getBlockHardness(world, blockPos) >= 0 && !(world.getBlockState(blockPos).getBlock() instanceof IFluidBlock) && !(world.getBlockState(blockPos).getBlock() instanceof BlockFlowingFluid)) {
                    Minecraft.getInstance().renderGlobal.drawSelectionBoundingBox(world.getBlockState(blockPos).getBlock().getShape(world.getBlockState(blockPos), world, blockPos).getBoundingBox().offset(-x, -y, -z).offset(blockPos).
                            grow(0.001), 0.0F, 0.0F, 0.0F, 0.4F);
                }
            });
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre event) {
        if (event.getEntityPlayer().getUniqueID().equals(Minecraft.getInstance().player.getUniqueID()) && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
            return;
        if (event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem().equals(ModuleTool.INFINITY_DRILL))
            event.getEntityPlayer().setActiveHand(EnumHand.MAIN_HAND);
        else if (event.getEntityPlayer().getHeldItem(EnumHand.OFF_HAND).getItem().equals(ModuleTool.INFINITY_DRILL))
            event.getEntityPlayer().setActiveHand(EnumHand.OFF_HAND);
    }
}