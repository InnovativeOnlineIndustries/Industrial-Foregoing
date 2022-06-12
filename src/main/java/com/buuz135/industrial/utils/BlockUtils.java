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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.module.ModuleCore;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class BlockUtils {

    public static BiPredicate<Level, BlockPos> CLAIMED_CHUNK_CHECKER = (world, pos) -> true;

    public static List<BlockPos> getBlockPosInAABB(AABB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<>();
        for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; ++y) {
            for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; ++x) {
                for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; ++z) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static boolean isBlockstateInMaterial(BlockState state, Material[] materials) {
        for (Material material : materials) {
            if (state.getMaterial() == material) return true;
        }

        return false;
    }

    public static boolean isBlockTag(Level world, BlockPos pos, TagKey<Block> tag) {
        return isBlockStateTag(world.getBlockState(pos), tag);
    }

    public static boolean isBlockStateTag(BlockState state, TagKey<Block> tag) {
        return state.is(tag);
    }

    public static boolean isLog(Level world, BlockPos pos) {
        return isBlockTag(world, pos, BlockTags.LOGS);
    }

    public static boolean isLeaves(Level world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.LEAVES || world.getBlockState(pos).is(BlockTags.WART_BLOCKS) || world.getBlockState(pos).is(BlockTags.LEAVES) || world.getBlockState(pos).getBlock().equals(Blocks.SHROOMLIGHT);
    }

    public static boolean isChorus(Level world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().equals(Blocks.CHORUS_PLANT) || world.getBlockState(pos).getBlock().equals(Blocks.CHORUS_FLOWER);
    }

    public static boolean canBlockBeBroken(Level world, BlockPos pos) {
        //if (world.isAirBlock(pos)) return false;
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), IndustrialForegoing.getFakePlayer(world));
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static boolean canBlockBeBrokenPlugin(Level world, BlockPos pos) {
        return CLAIMED_CHUNK_CHECKER.test(world, pos);
    }

    public static List<ItemStack> getBlockDrops(Level world, BlockPos pos) {
        return getBlockDrops(world, pos, 0);
    }

    public static List<ItemStack> getBlockDrops(Level world, BlockPos pos, int fortune) {
        BlockState state = world.getBlockState(pos);
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos)));
        return stacks;
    }

    public static boolean spawnItemStack(ItemStack stack, Level world, BlockPos pos) {
        ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, stack);
        item.setDeltaMovement(0, -1, 0);
        item.setPickUpDelay(40);
        item.setItem(stack);
        return world.addFreshEntity(item);
    }

    public static int getStackAmountByRarity(Rarity rarity){
        if (rarity.equals(Rarity.COMMON)) return 64*32;
        if (rarity.equals(ModuleCore.PITY_RARITY)) return 64*32*8;
        if (rarity.equals(ModuleCore.SIMPLE_RARITY)) return 64*32*16*16;
        if (rarity.equals(ModuleCore.ADVANCED_RARITY)) return 64*32*32*32*32;
        return Integer.MAX_VALUE;
    }

    public static int getFluidAmountByRarity(Rarity rarity){
        if (rarity.equals(Rarity.COMMON)) return 16*1000;
        if (rarity.equals(ModuleCore.PITY_RARITY)) return 16*1000*4;
        if (rarity.equals(ModuleCore.SIMPLE_RARITY)) return 16*1000*8*8;
        if (rarity.equals(ModuleCore.ADVANCED_RARITY)) return 16*1000*16*16*16;
        return Integer.MAX_VALUE;
    }

    public static void renderLaserBeam(BlockEntity tile, double x, double y, double z, Direction direction, float partialTicks, int length) {
//        Tesselator tess = Tesselator.getInstance();
//        RenderSystem.pushMatrix();
//        double tempX = x;
//        double tempY = y;
//        double tempZ = z;
//        switch (direction) {
//            case NORTH:
//                RenderSystem.rotatef(270, 1, 0, 0);
//                tempY = -z;
//                tempZ = y;
//                break;
//            case SOUTH:
//                RenderSystem.rotatef(90, 1, 0, 0);
//                tempY = z + 1;
//                tempZ = -y - 1;
//                break;
//            case EAST:
//                RenderSystem.rotatef(270, 0, 0, 1);
//                tempY = x + 1;
//                tempX = -y - 1;
//                break;
//            case WEST:
//                RenderSystem.rotatef(90, 0, 0, 1);
//                tempY = -x;
//                tempX = y;
//                break;
//            default:
//                tempY -= length;
//
//        }
//        Lighting.turnOff();
//        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
//        GL11.glDisable(GL11.GL_CULL_FACE);
//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glDepthMask(true);
//        BufferBuilder buffer = tess.getBuilder();
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
//        float d1 = -(tile.getLevel().getGameTime() % 15) / 15f;
//        float d2 = (tile.getLevel().getGameTime() % 40) / 2f;
//        double pointA = 0.45 - d2 / 200D;
//        if (d2 >= 10) {
//            pointA = 0.35 + d2 / 200D;
//        }
//        double pointB = 1 - pointA;
//        float uStart = 0.0f;
//        float uEnd = 1.0f;
//        float vStart = -1.0F + d1;
//        float vEnd = (length * 2) + vStart;
//        buffer.vertex(tempX + pointA, tempY + length, tempZ + pointA).uv(uEnd, vEnd).endVertex();
//        buffer.vertex(tempX + pointA, tempY, tempZ + pointA).uv(uEnd, vStart).endVertex();
//        buffer.vertex(tempX + pointB, tempY, tempZ + pointA).uv(uStart, vStart).endVertex();
//        buffer.vertex(tempX + pointB, tempY + length, tempZ + pointA).uv(uStart, vEnd).endVertex();
//        buffer.vertex(tempX + pointB, tempY + length, tempZ + pointB).uv(uEnd, vEnd).endVertex();
//        buffer.vertex(tempX + pointB, tempY, tempZ + pointB).uv(uEnd, vStart).endVertex();
//        buffer.vertex(tempX + pointA, tempY, tempZ + pointB).uv(uStart, vStart).endVertex();
//        buffer.vertex(tempX + pointA, tempY + length, tempZ + pointB).uv(uStart, vEnd).endVertex();
//        buffer.vertex(tempX + pointB, tempY + length, tempZ + pointA).uv(uEnd, vEnd).endVertex();
//        buffer.vertex(tempX + pointB, tempY, tempZ + pointA).uv(uEnd, vStart).endVertex();
//        buffer.vertex(tempX + pointB, tempY, tempZ + pointB).uv(uStart, vStart).endVertex();
//        buffer.vertex(tempX + pointB, tempY + length, tempZ + pointB).uv(uStart, vEnd).endVertex();
//        buffer.vertex(tempX + pointA, tempY + length, tempZ + pointB).uv(uEnd, vEnd).endVertex();
//        buffer.vertex(tempX + pointA, tempY, tempZ + pointB).uv(uEnd, vStart).endVertex();
//        buffer.vertex(tempX + pointA, tempY, tempZ + pointA).uv(uStart, vStart).endVertex();
//        buffer.vertex(tempX + pointA, tempY + length, tempZ + pointA).uv(uStart, vEnd).endVertex();
//        tess.end();
//        //RenderSystem.setupGui3DDiffuseLighting();
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        //GL11.glEnable(GL11.GL_BLEND);
//
//        RenderSystem.popMatrix();
    }
}
