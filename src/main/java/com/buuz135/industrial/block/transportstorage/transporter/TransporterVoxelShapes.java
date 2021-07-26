package com.buuz135.industrial.block.transportstorage.transporter;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.stream.Stream;

public class TransporterVoxelShapes {

    public static VoxelShape DOWN_RING = Shapes.joinUnoptimized(Block.box(4, 0, 4, 12, 1, 12), Block.box(6, 0, 6, 10, 1, 10), BooleanOp.ONLY_FIRST);
    public static VoxelShape DOWN_MIDDLE_EXTRACT = Block.box(6, 1, 6, 10, 1.5, 10);
    public static VoxelShape DOWN_MIDDLE_INSERT = Block.box(6, 0, 6, 10, 0.5, 10);

    public static VoxelShape NORTH_RING = Stream.of(
            Block.box(4, 4, 0, 12, 6, 1),
            Block.box(10, 6, 0, 12, 10, 1),
            Block.box(4, 6, 0, 6, 10, 1),
            Block.box(4, 10, 0, 12, 12, 1)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static VoxelShape NORTH_MIDDLE_EXTRACT = Block.box(6, 6, 1, 10, 10, 1.5);
    public static VoxelShape NORTH_MIDDLE_INSERT = Block.box(6, 6, 0, 10, 10, 0.5);

    public static VoxelShape EAST_RING = Stream.of(
            Block.box(15, 4, 4, 16, 6, 12),
            Block.box(15, 6, 10, 16, 10, 12),
            Block.box(15, 6, 4, 16, 10, 6),
            Block.box(15, 10, 4, 16, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static VoxelShape EAST_MIDDLE_EXTRACT = Block.box(14.5, 6, 6, 15, 10, 10);
    public static VoxelShape EAST_MIDDLE_INSERT = Block.box(15.5, 6, 6, 16, 10, 10);

    public static VoxelShape WEST_RING = Stream.of(
            Block.box(0, 4, 4, 1, 6, 12),
            Block.box(0, 6, 10, 1, 10, 12),
            Block.box(0, 6, 4, 1, 10, 6),
            Block.box(0, 10, 4, 1, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static VoxelShape WEST_MIDDLE_EXTRACT = Block.box(1, 6, 6, 1.5, 10, 10);
    public static VoxelShape WEST_MIDDLE_INSERT = Block.box(0, 6, 6, 0.5, 10, 10);

    public static VoxelShape SOUTH_RING = Stream.of(
            Block.box(4, 4, 15, 12, 6, 16),
            Block.box(10, 6, 15, 12, 10, 16),
            Block.box(4, 6, 15, 6, 10, 16),
            Block.box(4, 10, 15, 12, 12, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static VoxelShape SOUTH_MIDDLE_EXTRACT = Block.box(6, 6, 14.5, 10, 10, 15);
    public static VoxelShape SOUTH_MIDDLE_INSERT = Block.box(6, 6, 15.5, 10, 10, 16);

    public static VoxelShape UP_RING = Stream.of(
            Block.box(4, 15, 10, 12, 16, 12),
            Block.box(10, 15, 6, 12, 16, 10),
            Block.box(4, 15, 6, 6, 16, 10),
            Block.box(4, 15, 4, 12, 16, 6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static VoxelShape UP_MIDDLE_EXTRACT = Block.box(6, 15, 6, 10, 15.5, 10);
    public static VoxelShape UP_MIDDLE_INSERT = Block.box(6, 15.5, 6, 10, 16, 10);
}
