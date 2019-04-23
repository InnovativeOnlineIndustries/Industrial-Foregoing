package com.buuz135.industrial.proxy.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class DistanceRayTraceResult extends RayTraceResult implements Comparable<DistanceRayTraceResult> {
    public double dist;

    public DistanceRayTraceResult(Vec3d hitVecIn, BlockPos blockPosIn, EnumFacing sideHitIn, Cuboid box, double dist) {
        super(hitVecIn, sideHitIn, blockPosIn);
        hitInfo = box;
        this.dist = dist;
    }

    @Override
    public int compareTo(DistanceRayTraceResult o) {
        return Double.compare(dist, o.dist);
    }
}
