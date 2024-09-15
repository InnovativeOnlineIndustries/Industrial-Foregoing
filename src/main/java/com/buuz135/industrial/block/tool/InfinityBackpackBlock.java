package com.buuz135.industrial.block.tool;

import com.buuz135.industrial.block.tool.tile.InfinityBackpackTile;
import com.buuz135.industrial.module.ModuleTool;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

public class InfinityBackpackBlock extends RotatableBlock<InfinityBackpackTile> {

    public static VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(1.5, 0, 6, 14.5, 17, 15),
            Block.box(-1.5, 1, 8, 4.5, 7, 14),
            Block.box(1.5, 0, 3, 14.5, 10, 6),
            Block.box(11.5, 1, 8, 17.5, 7, 14),
            Block.box(1.25, 12, 5.75, 12.25, 14, 13.75),
            Block.box(2.5, 6, 2.75, 13.5, 8, 10.75),
            Block.box(3.5, 2, 15, 7.5, 17, 17),
            Block.box(8.5, 2, 15, 12.5, 17, 17),
            Block.box(4.5, 17, 12, 11.5, 19, 14),
            Block.box(4.5, -1, 2, 11.5, 1, 9),
            Block.box(5.5, 0, 1, 10.5, 4, 3),
            Block.box(5, -0.5, 0.5, 11, 4.5, 3.5),
            Block.box(6.5, 18.25, 12.5, 9.5, 19.25, 13.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST_SHAPE = Stream.of(
            Block.box(1, 0, 1.5, 10, 17, 14.5),
            Block.box(2, 1, -1.5, 8, 7, 4.5),
            Block.box(10, 0, 1.5, 13, 10, 14.5),
            Block.box(2, 1, 11.5, 8, 7, 17.5),
            Block.box(2.25, 12, 1.25, 10.25, 14, 12.25),
            Block.box(5.25, 6, 2.5, 13.25, 8, 13.5),
            Block.box(-1, 2, 3.5, 1, 17, 7.5),
            Block.box(-1, 2, 8.5, 1, 17, 12.5),
            Block.box(2, 17, 4.5, 4, 19, 11.5),
            Block.box(7, -1, 4.5, 14, 1, 11.5),
            Block.box(13, 0, 5.5, 15, 4, 10.5),
            Block.box(12.5, -0.5, 5, 15.5, 4.5, 11),
            Block.box(2.5, 18.25, 6.5, 3.5, 19.25, 9.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH_SHAPE = Stream.of(
            Block.box(1.5, 0, 1, 14.5, 17, 10),
            Block.box(11.5, 1, 2, 17.5, 7, 8),
            Block.box(1.5, 0, 10, 14.5, 10, 13),
            Block.box(-1.5, 1, 2, 4.5, 7, 8),
            Block.box(3.75, 12, 2.25, 14.75, 14, 10.25),
            Block.box(2.5, 6, 5.25, 13.5, 8, 13.25),
            Block.box(8.5, 2, -1, 12.5, 17, 1),
            Block.box(3.5, 2, -1, 7.5, 17, 1),
            Block.box(4.5, 17, 2, 11.5, 19, 4),
            Block.box(4.5, -1, 7, 11.5, 1, 14),
            Block.box(5.5, 0, 13, 10.5, 4, 15),
            Block.box(5, -0.5, 12.5, 11, 4.5, 15.5),
            Block.box(6.5, 18.25, 2.5, 9.5, 19.25, 3.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST_SHAPE = Stream.of(
            Block.box(6, 0, 1.5, 15, 17, 14.5),
            Block.box(8, 1, 11.5, 14, 7, 17.5),
            Block.box(3, 0, 1.5, 6, 10, 14.5),
            Block.box(8, 1, -1.5, 14, 7, 4.5),
            Block.box(5.75, 12, 3.75, 13.75, 14, 14.75),
            Block.box(2.75, 6, 2.5, 10.75, 8, 13.5),
            Block.box(15, 2, 8.5, 17, 17, 12.5),
            Block.box(15, 2, 3.5, 17, 17, 7.5),
            Block.box(12, 17, 4.5, 14, 19, 11.5),
            Block.box(2, -1, 4.5, 9, 1, 11.5),
            Block.box(1, 0, 5.5, 3, 4, 10.5),
            Block.box(0.5, -0.5, 5, 3.5, 4.5, 11),
            Block.box(12.5, 18.25, 6.5, 13.5, 19.25, 9.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public InfinityBackpackBlock() {
        super("infinity_backpack", Properties.ofFullCopy(Blocks.IRON_BLOCK), InfinityBackpackTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return InfinityBackpackTile::new;
    }

    @Override
    public @NotNull RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootParams.Builder builder) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        BlockEntity tile = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof InfinityBackpackTile infinityBackpackTile) {
            stacks.add(infinityBackpackTile.getBackpack());
        }
        return stacks;
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        return blockLootTables.droppingNothing();
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return NonNullList.create();
    }

    @Override
    public Item asItem() {
        return ModuleTool.INFINITY_BACKPACK.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof InfinityBackpackTile infinityBackpackTile) {
            return infinityBackpackTile.getBackpack();
        }
        return new ItemStack(ModuleTool.INFINITY_BACKPACK.get());
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        var direction = state.getValue(RotatableBlock.FACING_HORIZONTAL);
        if (direction == Direction.NORTH) {
            return NORTH_SHAPE;
        }
        if (direction == Direction.SOUTH) {
            return SOUTH_SHAPE;
        }
        if (direction == Direction.EAST) {
            return EAST_SHAPE;
        }
        if (direction == Direction.WEST) {
            return WEST_SHAPE;
        }
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var direction = state.getValue(RotatableBlock.FACING_HORIZONTAL);
        if (direction == Direction.NORTH) {
            return NORTH_SHAPE;
        }
        if (direction == Direction.SOUTH) {
            return SOUTH_SHAPE;
        }
        if (direction == Direction.EAST) {
            return EAST_SHAPE;
        }
        if (direction == Direction.WEST) {
            return WEST_SHAPE;
        }
        return super.getShape(state, level, pos, context);
    }

}
