package com.buuz135.industrial.utils;

import com.buuz135.industrial.proxy.block.BlockConveyor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MovementUtils {

    public static void handleConveyorMovement(Entity entity, EnumFacing direction, BlockPos pos, BlockConveyor.EnumType type) {
        if (entity instanceof EntityPlayer && entity.isSneaking()) return;
        if (entity.posY - pos.getY() > 0.3 && !type.isVertical()) return;

        if ((direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH) && ((entity.posX - pos.getX() < 0.05) || (entity.posX - pos.getX() > 0.95)))
            return;
        if ((direction == EnumFacing.EAST || direction == EnumFacing.WEST) && ((entity.posZ - pos.getZ() < 0.05) || (entity.posZ - pos.getZ() > 0.95)))
            return;

        double speed = 0.2;
        if (type.isFast()) speed *= 2;
        Vec3d vec3d = new Vec3d(speed * direction.getDirectionVec().getX(), speed * direction.getDirectionVec().getY(), speed * direction.getDirectionVec().getZ());
        if (type.isVertical()) {
            vec3d = vec3d.addVector(0, type.isUp() ? 0.258 : -0.05, 0);
            entity.onGround = false;
        }

        entity.motionX = vec3d.x;
        if (vec3d.y != 0) entity.motionY = vec3d.y;
        entity.motionZ = vec3d.z;
    }
}
