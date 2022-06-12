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

package com.buuz135.industrial.block.tile;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.function.BiFunction;

public class RangeManager {

    private BlockPos current;
    private AABB box;
    private RangeType type;
    private Direction direction;

    public RangeManager(BlockPos current, Direction facing, RangeType rangeType) {
        this.current = current;
        this.type = rangeType;
        this.direction = facing;
        this.box = rangeType.getOffsetCreation().apply(facing, new AABB(0, 0, 0, 1, 1, 1).move(current));
    }

    public VoxelShape get(int range) {
        return Shapes.create(type.getOffsetRange().apply(this, range));
    }

    public BlockPos getCurrent() {
        return current;
    }

    public AABB getBox() {
        return box;
    }

    public RangeType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public enum RangeType {
        FRONT((direction, axisAlignedBB) -> axisAlignedBB.move(direction.getNormal().getX(), direction.getNormal().getY(), direction.getNormal().getZ()),
                (rangeManager, integer) -> rangeManager.getBox().move(rangeManager.getDirection().getNormal().getX() * integer, rangeManager.getDirection().getNormal().getY() * integer, rangeManager.getDirection().getNormal().getZ() * integer).
                        inflate(integer, 0, integer)),
        BEHIND((direction, axisAlignedBB) -> axisAlignedBB.move(direction.getOpposite().getNormal().getX(), direction.getOpposite().getNormal().getY(), direction.getOpposite().getNormal().getZ()),
                (rangeManager, integer) -> rangeManager.getBox().move(rangeManager.getDirection().getOpposite().getNormal().getX() * integer, rangeManager.getDirection().getOpposite().getNormal().getY() * integer, rangeManager.getDirection().getOpposite().getNormal().getZ() * integer).
                        inflate(integer, 0, integer)),
        TOP((direction, axisAlignedBB) -> axisAlignedBB.move(0, 1, 0), (rangeManager, integer) -> rangeManager.getBox().inflate(integer, 0, integer)),
        TOP_UP((direction, axisAlignedBB) -> axisAlignedBB.move(0, 2, 0), (rangeManager, integer) -> rangeManager.getBox().inflate(integer, 0, integer)),
        BOTTOM((direction, axisAlignedBB) -> axisAlignedBB.move(0, -1, 0), (rangeManager, integer) -> rangeManager.getBox().inflate(integer, 0, integer));


        private final BiFunction<Direction, AABB, AABB> offsetCreation;
        private final BiFunction<RangeManager, Integer, AABB> offsetRange;

        RangeType(BiFunction<Direction, AABB, AABB> offsetCreation, BiFunction<RangeManager, Integer, AABB> offsetRange) {
            this.offsetCreation = offsetCreation;
            this.offsetRange = offsetRange;
        }

        public BiFunction<Direction, AABB, AABB> getOffsetCreation() {
            return offsetCreation;
        }

        public BiFunction<RangeManager, Integer, AABB> getOffsetRange() {
            return offsetRange;
        }
    }

}
