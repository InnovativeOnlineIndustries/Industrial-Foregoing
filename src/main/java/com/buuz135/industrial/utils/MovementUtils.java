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

import com.buuz135.industrial.proxy.block.BlockConveyor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class MovementUtils {

    public static void handleConveyorMovement(Entity entity, EnumFacing direction, BlockPos pos, BlockConveyor.EnumType type) {
        if (entity instanceof EntityPlayer && entity.isSneaking()) return;
        if (entity.posY - pos.getY() > 0.3 && !type.isVertical()) return;
        try {
            AxisAlignedBB collision = entity.world.getBlockState(pos).getBlock().getCollisionBoundingBox(entity.world.getBlockState(pos), entity.world, pos).offset(pos);
            if (!type.isVertical() && !collision.grow(0.01).intersects(entity.getEntityBoundingBox())) return;

            //DIRECTION MOVEMENT
            double speed = 0.2;
            if (type.isFast()) speed *= 2;
            Vec3d vec3d = new Vec3d(speed * direction.getDirectionVec().getX(), speed * direction.getDirectionVec().getY(), speed * direction.getDirectionVec().getZ());
            if (type.isVertical()) {
                vec3d = vec3d.add(0, type.isUp() ? 0.258 : -0.05, 0);
                entity.onGround = false;
            }

            //CENTER
            if (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH) {
                if (entity.posX - pos.getX() < 0.45) {
                    vec3d = vec3d.add(0.08, 0, 0);
                } else if (entity.posX - pos.getX() > 0.55) {
                    vec3d = vec3d.add(-0.08, 0, 0);
                }
            }
            if (direction == EnumFacing.EAST || direction == EnumFacing.WEST) {
                if (entity.posZ - pos.getZ() < 0.45) {
                    vec3d = vec3d.add(0, 0, 0.08);
                } else if (entity.posZ - pos.getZ() > 0.55) {
                    vec3d = vec3d.add(0, 0, -0.08);
                }
            }

            entity.motionX = vec3d.x;
            if (vec3d.y != 0) entity.motionY = vec3d.y;
            entity.motionZ = vec3d.z;
        } catch (Exception e) {
            System.out.println("Found error while trying to move " + entity.toString() + " at position " + pos.toString());
            e.printStackTrace();
        }
    }

    public static void handleConveyorMovement(Entity entity, EnumFacing direction, BlockPos pos, BlockConveyor.EnumType type, List<Entity> filter) {
        if (filter.contains(entity)) return;
        handleConveyorMovement(entity, direction, pos, type);
    }
}
