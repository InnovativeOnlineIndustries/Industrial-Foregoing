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
package com.buuz135.industrial.api.conveyor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface IConveyorContainer {

    World getConveyorWorld();

    BlockPos getConveyorPosition();

    void requestSync();

    void requestFluidSync();

    boolean hasUpgrade(EnumFacing facing);

    void addUpgrade(EnumFacing facing, ConveyorUpgradeFactory factory);

    void removeUpgrade(EnumFacing facing, boolean drop);

    List<Integer> getEntityFilter();

    class Empty implements IConveyorContainer {
        @Override
        public World getConveyorWorld() {
            return null;
        }

        @Override
        public BlockPos getConveyorPosition() {
            return null;
        }

        @Override
        public void requestSync() {

        }

        @Override
        public void requestFluidSync() {

        }

        @Override
        public boolean hasUpgrade(EnumFacing facing) {
            return false;
        }

        @Override
        public void addUpgrade(EnumFacing facing, ConveyorUpgradeFactory factory) {

        }

        @Override
        public void removeUpgrade(EnumFacing facing, boolean drop) {

        }

        @Override
        public List<Integer> getEntityFilter() {
            return new ArrayList<>();
        }
    }
}