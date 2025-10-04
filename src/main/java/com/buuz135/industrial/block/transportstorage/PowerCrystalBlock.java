package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.block.transportstorage.tile.PowerCrystalTile;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.utils.CustomRarity;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class PowerCrystalBlock extends BasicTileBlock<PowerCrystalTile> {

    public static VoxelShape SHAPE = Stream.of(
            Block.box(5, 4, 5, 11, 5, 11),
            Block.box(6, 5, 6, 10, 12, 10),
            Block.box(5, 12, 5, 11, 13, 11),
            Block.box(5.5, 10, 5.5, 10.5, 11, 10.5),
            Block.box(5.5, 8, 5.5, 10.5, 9, 10.5),
            Block.box(5.5, 6, 5.5, 10.5, 7, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private final Rarity rarity;

    public PowerCrystalBlock(Rarity rarity) {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK), PowerCrystalTile.class);
        this.rarity = rarity;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) -> new PowerCrystalTile(this, getBlock().type().get(), blockPos, blockState);
    }

    private BlockWithTile getBlock() {
        if (rarity == CustomRarity.PITY.getValue()) return ModuleTransportStorage.PITY_POWER_CRYSTAL;
        if (rarity == CustomRarity.SIMPLE.getValue()) return ModuleTransportStorage.SIMPLE_POWER_CRYSTAL;
        if (rarity == CustomRarity.ADVANCED.getValue()) return ModuleTransportStorage.ADVANCED_POWER_CRYSTAL;
        return ModuleTransportStorage.SUPREME_POWER_CRYSTAL;
    }

    public int getMaxDistance() {
        if (rarity == CustomRarity.PITY.getValue()) return 32;
        if (rarity == CustomRarity.SIMPLE.getValue()) return 32 * 2;
        if (rarity == CustomRarity.ADVANCED.getValue()) return 32 * 4;
        return 32 * 8;
    }

    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
