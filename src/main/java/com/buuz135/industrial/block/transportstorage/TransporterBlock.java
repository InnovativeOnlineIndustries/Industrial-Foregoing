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
package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.util.RayTraceUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class TransporterBlock extends BasicTileBlock<TransporterTile> implements SimpleWaterloggedBlock {

    public TransporterBlock(CreativeModeTab group) {
        super("transporter", Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_ORANGE).noCollission().strength(2.0f), TransporterTile.class);
        //this.setRegistryName(Reference.MOD_ID, "transporter");
        this.setItemGroup(group);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        //super.fillItemGroup(group, items);
    }

    private static List<VoxelShape> getShapes(BlockState state, BlockGetter source, BlockPos pos, Predicate<TransporterType> filter) {
        List<VoxelShape> boxes = new ArrayList<>();
        BlockEntity entity = source.getBlockEntity(pos);
        if (entity instanceof TransporterTile) {
            for (TransporterType upgrade : ((TransporterTile) entity).getTransporterTypeMap().values())
                if (upgrade != null && filter.test(upgrade)) {
                    boxes.add(upgrade.getCenterBoundingBox());
                    boxes.add(upgrade.getBorderBoundingBox());
                }
        }
        return boxes;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<TransporterTile> getTileEntityFactory() {
        return TransporterTile::new;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TransporterTile) {
            if (target instanceof BlockHitResult) {
                Pair<Direction, Boolean> upgradePair = getFacingUpgradeHit(state, player.level, pos, player);
                if (upgradePair != null) {
                    TransporterType upgrade = ((TransporterTile) tileEntity).getTransporterTypeMap().get(upgradePair.getKey());
                    if (upgrade != null) {
                        return new ItemStack(upgrade.getFactory().getUpgradeItem(), 1);
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public Pair<Direction, Boolean> getFacingUpgradeHit(BlockState state, Level worldIn, BlockPos pos, Player player) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        HitResult result = RayTraceUtils.rayTraceSimple(worldIn, player, 32, 0);
        if (result instanceof BlockHitResult) {
            VoxelShape hit = RayTraceUtils.rayTraceVoxelShape((BlockHitResult) result, worldIn, player, 32, 0);
            if (hit != null && tileEntity instanceof TransporterTile) {
                for (Direction direction : ((TransporterTile) tileEntity).getTransporterTypeMap().keySet()) {
                    if (Shapes.joinIsNotEmpty(((TransporterTile) tileEntity).getTransporterTypeMap().get(direction).getCenterBoundingBox(), hit, BooleanOp.AND)) {
                        return Pair.of(direction, true);
                    }
                    if (Shapes.joinIsNotEmpty(((TransporterTile) tileEntity).getTransporterTypeMap().get(direction).getBorderBoundingBox(), hit, BooleanOp.AND)) {
                        return Pair.of(direction, false);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<VoxelShape> getBoundingBoxes(BlockState state, BlockGetter source, BlockPos pos) {
        return getShapes(state, source, pos, conveyorUpgrade -> true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext p_220053_4_) {
        return getShapes(state, world, pos, transporterType -> true).stream().reduce((voxelShape, voxelShape2) -> Shapes.joinUnoptimized(voxelShape, voxelShape2, BooleanOp.OR)).orElseGet(Shapes::empty);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        VoxelShape shape = Shapes.empty();
        for (VoxelShape shape1 : getShapes(state, world, pos, conveyorUpgrade -> !conveyorUpgrade.ignoresCollision())) {
            shape = Shapes.join(shape, shape1, BooleanOp.OR);
        }
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @Override
    public boolean hasIndividualRenderVoxelShape() {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        ItemStack handStack = player.getItemInHand(hand);
        if (tileEntity instanceof TransporterTile) {
            Pair<Direction, Boolean> info = getFacingUpgradeHit(state, worldIn, pos, player);
            if (info != null) {
                Direction facing = info.getLeft();
                boolean isMiddle = info.getRight();
                if (player.isShiftKeyDown() && handStack.isEmpty()) {
                    if (((TransporterTile) tileEntity).hasUpgrade(facing)) {
                        ((TransporterTile) tileEntity).removeUpgrade(facing, true);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    if (isMiddle) {
                        if (((TransporterTile) tileEntity).hasUpgrade(facing)) {
                            ((TransporterTile) tileEntity).getTransporterTypeMap().get(facing).toggleAction();
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        if (((TransporterTile) tileEntity).hasUpgrade(facing)) {
                            TransporterType transporterType = ((TransporterTile) tileEntity).getTransporterTypeMap().get(facing);
                            if (!handStack.isEmpty() && transporterType.onUpgradeActivated(player, hand)) {
                                return InteractionResult.SUCCESS;
                            } else if (transporterType.hasGui()) {
                                ((TransporterTile) tileEntity).openGui(player, facing);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return super.use(state, worldIn, pos, player, hand, ray);
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof TransporterTile) {
            ((TransporterTile) tileEntity).getTransporterTypeMap().values().forEach(transporterType -> itemStacks.addAll(transporterType.getDrops()));
        }
        return itemStacks;
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        return blockLootTables.droppingNothing();
    }
}
