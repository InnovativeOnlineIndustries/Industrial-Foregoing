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