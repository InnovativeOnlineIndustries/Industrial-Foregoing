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

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.function.BiFunction;

public class RangeManager {

    private BlockPos current;
    private AxisAlignedBB box;
    private RangeType type;
    private Direction direction;

    public RangeManager(BlockPos current, Direction facing, RangeType rangeType) {
        this.current = current;
        this.type = rangeType;
        this.direction = facing;
        this.box = rangeType.getOffsetCreation().apply(facing, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(current));
    }

    public VoxelShape get(int range) {
        return VoxelShapes.create(type.getOffsetRange().apply(this, range));
    }

    public BlockPos getCurrent() {
        return current;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public RangeType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public enum RangeType {
        FRONT((direction, axisAlignedBB) -> axisAlignedBB.offset(direction.getDirectionVec().getX(), direction.getDirectionVec().getY(), direction.getDirectionVec().getZ()),
                (rangeManager, integer) -> rangeManager.getBox().offset(rangeManager.getDirection().getDirectionVec().getX() * integer, rangeManager.getDirection().getDirectionVec().getY() * integer, rangeManager.getDirection().getDirectionVec().getZ() * integer).
                        grow(integer, 0, integer)),
        BEHIND((direction, axisAlignedBB) -> axisAlignedBB.offset(direction.getOpposite().getDirectionVec().getX(), direction.getOpposite().getDirectionVec().getY(), direction.getOpposite().getDirectionVec().getZ()),
                (rangeManager, integer) -> rangeManager.getBox().offset(rangeManager.getDirection().getOpposite().getDirectionVec().getX() * integer, rangeManager.getDirection().getOpposite().getDirectionVec().getY() * integer, rangeManager.getDirection().getOpposite().getDirectionVec().getZ() * integer).
                        grow(integer, 0, integer)),
        TOP((direction, axisAlignedBB) -> axisAlignedBB.offset(0, 1, 0), (rangeManager, integer) -> rangeManager.getBox().grow(integer, 0, integer)),
        TOP_UP((direction, axisAlignedBB) -> axisAlignedBB.offset(0, 2, 0), (rangeManager, integer) -> rangeManager.getBox().grow(integer, 0, integer)),
        BOTTOM((direction, axisAlignedBB) -> axisAlignedBB.offset(0, -1, 0), (rangeManager, integer) -> rangeManager.getBox().grow(integer, 0, integer));


        private final BiFunction<Direction, AxisAlignedBB, AxisAlignedBB> offsetCreation;
        private final BiFunction<RangeManager, Integer, AxisAlignedBB> offsetRange;

        RangeType(BiFunction<Direction, AxisAlignedBB, AxisAlignedBB> offsetCreation, BiFunction<RangeManager, Integer, AxisAlignedBB> offsetRange) {
            this.offsetCreation = offsetCreation;
            this.offsetRange = offsetRange;
        }

        public BiFunction<Direction, AxisAlignedBB, AxisAlignedBB> getOffsetCreation() {
            return offsetCreation;
        }

        public BiFunction<RangeManager, Integer, AxisAlignedBB> getOffsetRange() {
            return offsetRange;
        }
    }

}
