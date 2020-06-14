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

import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.module.ModuleTool;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import org.apache.commons.lang3.tuple.Pair;

public class IFClientEvents {

    @SubscribeEvent
    public void textureStich(TextureStitchEvent.Pre pre) {
        //pre.getMap().registerSprite(Minecraft.getInstance().getResourceManager(), new ResourceLocation(Reference.MOD_ID, "blocks/catears")); TODO
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_RAW.getStill());
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_RAW.getFlowing());
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_FERMENTED.getStill());
        //pre.getMap().registerSprite(FluidsRegistry.ORE_FLUID_FERMENTED.getFlowing());
    }

    @SubscribeEvent
    public void blockOverlayEvent(DrawHighlightEvent event) {
        RayTraceResult hit = event.getTarget();
        if (hit.getType() == RayTraceResult.Type.BLOCK && Minecraft.getInstance().player.getHeldItemMainhand().getItem().equals(ModuleTool.INFINITY_DRILL)) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) hit;
            event.setCanceled(true);
            ItemStack hand = Minecraft.getInstance().player.getHeldItemMainhand();
            InfinityTier tier = ModuleTool.INFINITY_DRILL.getSelectedTier(hand);
            World world = Minecraft.getInstance().player.world;
            Pair<BlockPos, BlockPos> area = ModuleTool.INFINITY_DRILL.getArea(blockRayTraceResult.getPos(), blockRayTraceResult.getFace(), tier, false);
            MatrixStack stack = new MatrixStack();
            stack.push();
            ActiveRenderInfo info = event.getInfo();
            stack.rotate(Vector3f.XP.rotationDegrees(info.getPitch()));
            stack.rotate(Vector3f.YP.rotationDegrees(info.getYaw() + 180));
            double d0 = info.getProjectedView().getX();
            double d1 = info.getProjectedView().getY();
            double d2 = info.getProjectedView().getZ();
            IVertexBuilder builder = Minecraft.getInstance().getRenderTypeBuffers().getOutlineBufferSource().getBuffer(RenderType.getLines());
            BlockPos.getAllInBoxMutable(area.getLeft(), area.getRight()).forEach(blockPos -> {
                VoxelShape shape = world.getBlockState(blockPos).getShape(world, blockPos);
                if (shape != null && !shape.isEmpty() && !world.isAirBlock(blockPos) && world.getBlockState(blockPos).getBlockHardness(world, blockPos) >= 0 && !(world.getBlockState(blockPos).getBlock() instanceof IFluidBlock) && !(world.getBlockState(blockPos).getBlock() instanceof FlowingFluidBlock)) {
                    WorldRenderer.drawBoundingBox(stack, builder, shape.getBoundingBox().offset(blockPos.getX() - d0, blockPos.getY() - d1, blockPos.getZ() - d2), 0, 0, 0, 0.35F);
                }
            });
            stack.pop();
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre event) {
        if (event.getPlayer().getUniqueID().equals(Minecraft.getInstance().player.getUniqueID()) && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
            return;
        if (event.getPlayer().getHeldItem(Hand.MAIN_HAND).getItem().equals(ModuleTool.INFINITY_DRILL))
            event.getPlayer().setActiveHand(Hand.MAIN_HAND);
        else if (event.getPlayer().getHeldItem(Hand.OFF_HAND).getItem().equals(ModuleTool.INFINITY_DRILL))
            event.getPlayer().setActiveHand(Hand.OFF_HAND);
    }
}