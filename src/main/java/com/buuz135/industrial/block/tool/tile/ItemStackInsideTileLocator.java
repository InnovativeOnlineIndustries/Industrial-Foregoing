package com.buuz135.industrial.block.tool.tile;

import com.hrznstudio.titanium.itemstack.ItemStackHarnessRegistry;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import com.hrznstudio.titanium.network.locator.LocatorType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ItemStackInsideTileLocator extends LocatorInstance {

    public static final LocatorType STACK_INSIDE_TILE_ENTITY = new LocatorType("stack_inside_tile_entity", ItemStackInsideTileLocator::new);

    static {
        LocatorFactory.registerLocatorType(STACK_INSIDE_TILE_ENTITY);
    }

    private BlockPos blockPos;

    public ItemStackInsideTileLocator(BlockPos blockPos) {
        super(STACK_INSIDE_TILE_ENTITY);
        this.blockPos = blockPos;
    }

    public ItemStackInsideTileLocator() {
        super(STACK_INSIDE_TILE_ENTITY);
    }

    public Optional<?> locale(Player playerEntity) {
        var tile = playerEntity.getCommandSenderWorld().getBlockEntity(blockPos);
        if (tile instanceof InfinityBackpackTile infinityBackpackTile) {
            return ItemStackHarnessRegistry.createItemStackHarness(infinityBackpackTile.getBackpack());
        }
        return Optional.empty();
    }

    public ContainerLevelAccess getWorldPosCallable(Level world) {
        return ContainerLevelAccess.create(world, this.blockPos);
    }




}
