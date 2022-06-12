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

import com.buuz135.industrial.block.transportstorage.ConveyorBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MovementUtils {

    public static void handleConveyorMovement(Entity entity, Direction direction, BlockPos pos, ConveyorBlock.EnumType type) {
        if (entity instanceof Player && entity.isCrouching()) return;
        if (entity.blockPosition().getY() - pos.getY() > 0.3 && !type.isVertical()) return;

        VoxelShape collision = entity.level.getBlockState(pos).getBlock().getCollisionShape(entity.level.getBlockState(pos), entity.level, pos, CollisionContext.empty()).move(pos.getX(), pos.getY(), pos.getZ());
//        if (direction == Direction.NORTH || direction == Direction.SOUTH){
//            collision = collision.contract(-0.1,0,0);
//            collision = collision.contract(0.1,0,0);
//        }
//        if (direction == Direction.EAST || direction == Direction.WEST) {
//            collision = collision.contract(0,0,-0.1);
//            collision = collision.contract(0,0,0.1);
//        }
        if (!type.isVertical() && collision.toAabbs().stream().noneMatch(axisAlignedBB -> axisAlignedBB.inflate(0.01).intersects(entity.getBoundingBox())))
            return;
        //DIRECTION MOVEMENT
        double speed = 0.2;
        if (type.isFast()) speed *= 2;
        Vec3 vec3d = new Vec3(speed * direction.getNormal().getX(), speed * direction.getNormal().getY(), speed * direction.getNormal().getZ());
        if (type.isVertical()) {
            vec3d = vec3d.add(0, type.isUp() ? 0.258 : -0.05, 0);
            //entity.onGround = false;
        }

        //CENTER
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            if (entity.getX() - pos.getX() < 0.45) {
                vec3d = vec3d.add(0.08, 0, 0);
            } else if (entity.getX() - pos.getX() > 0.55) {
                vec3d = vec3d.add(-0.08, 0, 0);
            }
        }
        if (direction == Direction.EAST || direction == Direction.WEST) {
            if (entity.getZ() - pos.getZ() < 0.45) {
                vec3d = vec3d.add(0, 0, 0.08);
            } else if (entity.getZ() - pos.getZ() > 0.55) {
                vec3d = vec3d.add(0, 0, -0.08);
            }
        }
        entity.setDeltaMovement(vec3d.x, vec3d.y != 0 ? vec3d.y : entity.getDeltaMovement().y, vec3d.z);
    }

    public static void handleConveyorMovement(Entity entity, Direction direction, BlockPos pos, ConveyorBlock.EnumType type, List<Entity> filter) {
        if (filter.contains(entity)) return;
        handleConveyorMovement(entity, direction, pos, type);
    }
}
