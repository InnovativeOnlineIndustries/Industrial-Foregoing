package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.util.RayTraceUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class TransporterBlock extends BasicTileBlock<TransporterTile> implements IWaterLoggable {

    public TransporterBlock(ItemGroup group) {
        super(Properties.create(Material.ANVIL, MaterialColor.ADOBE).doesNotBlockMovement().hardnessAndResistance(2.0f), TransporterTile.class);
        this.setRegistryName(Reference.MOD_ID, "transporter");
        this.setItemGroup(group);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        //super.fillItemGroup(group, items);
    }

    private static List<VoxelShape> getShapes(BlockState state, IBlockReader source, BlockPos pos, Predicate<TransporterType> filter) {
        List<VoxelShape> boxes = new ArrayList<>();
        TileEntity entity = source.getTileEntity(pos);
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
    public IFactory<TransporterTile> getTileEntityFactory() {
        return TransporterTile::new;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TransporterTile) {
            if (target instanceof BlockRayTraceResult) {
                TransporterType upgrade = ((TransporterTile) tileEntity).getTransporterTypeMap().get(getFacingUpgradeHit(state, player.world, pos, player).getKey());
                if (upgrade != null) {
                    return new ItemStack(upgrade.getFactory().getUpgradeItem(), 1);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public Pair<Direction, Boolean> getFacingUpgradeHit(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        RayTraceResult result = RayTraceUtils.rayTraceSimple(worldIn, player, 32, 0);
        if (result instanceof BlockRayTraceResult) {
            VoxelShape hit = RayTraceUtils.rayTraceVoxelShape((BlockRayTraceResult) result, worldIn, player, 32, 0);
            if (hit != null && tileEntity instanceof TransporterTile) {
                for (Direction direction : ((TransporterTile) tileEntity).getTransporterTypeMap().keySet()) {
                    if (VoxelShapes.compare(((TransporterTile) tileEntity).getTransporterTypeMap().get(direction).getCenterBoundingBox(), hit, IBooleanFunction.AND)) {
                        return Pair.of(direction, true);
                    }
                    if (VoxelShapes.compare(((TransporterTile) tileEntity).getTransporterTypeMap().get(direction).getBorderBoundingBox(), hit, IBooleanFunction.AND)) {
                        return Pair.of(direction, false);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<VoxelShape> getBoundingBoxes(BlockState state, IBlockReader source, BlockPos pos) {
        return getShapes(state, source, pos, conveyorUpgrade -> true);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext p_220053_4_) {
        return getShapes(state, world, pos, transporterType -> true).stream().reduce((voxelShape, voxelShape2) -> VoxelShapes.combine(voxelShape, voxelShape2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selectionContext) {
        VoxelShape shape = VoxelShapes.empty();
        for (VoxelShape shape1 : getShapes(state, world, pos, conveyorUpgrade -> !conveyorUpgrade.ignoresCollision())) {
            shape = VoxelShapes.combineAndSimplify(shape, shape1, IBooleanFunction.OR);
        }
        return shape;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }


    @Override
    public boolean hasIndividualRenderVoxelShape() {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        ItemStack handStack = player.getHeldItem(hand);
        if (tileEntity instanceof TransporterTile) {
            Pair<Direction, Boolean> info = getFacingUpgradeHit(state, worldIn, pos, player);
            if (info != null) {
                Direction facing = info.getLeft();
                boolean isMiddle = info.getRight();
                if (player.isSneaking() && handStack.isEmpty()) {
                    if (((TransporterTile) tileEntity).hasUpgrade(facing)) {
                        ((TransporterTile) tileEntity).removeUpgrade(facing, true);
                        return ActionResultType.SUCCESS;
                    }
                } else {
                    if (isMiddle) {
                        if (((TransporterTile) tileEntity).hasUpgrade(facing)) {
                            ((TransporterTile) tileEntity).getTransporterTypeMap().get(facing).toggleAction();
                            return ActionResultType.SUCCESS;
                        }
                    } else {
                        if (((TransporterTile) tileEntity).hasUpgrade(facing)) {
                            TransporterType transporterType = ((TransporterTile) tileEntity).getTransporterTypeMap().get(facing);
                            if (!handStack.isEmpty() && transporterType.onUpgradeActivated(player, hand)) {
                                return ActionResultType.SUCCESS;
                            } else if (transporterType.hasGui()) {
                                ((TransporterTile) tileEntity).openGui(player, facing);
                                return ActionResultType.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, hand, ray);
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        TileEntity tileEntity = worldIn.getTileEntity(pos);
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
