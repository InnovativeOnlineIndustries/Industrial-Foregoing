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

package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.block.transportstorage.ConveyorBlock;
import com.buuz135.industrial.block.transportstorage.ConveyorBlock.EnumType;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.client.model.ConveyorModelData;
import com.buuz135.industrial.utils.MovementUtils;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.buuz135.industrial.block.transportstorage.ConveyorBlock.FACING;
import static com.buuz135.industrial.block.transportstorage.ConveyorBlock.TYPE;

public class ConveyorTile extends ActiveTile<ConveyorTile> implements IBlockContainer<ConveyorUpgradeFactory> {

    private Direction facing;
    private EnumType type;
    private int color;
    private Map<Direction, ConveyorUpgrade> upgradeMap = new HashMap<>();
    private List<Integer> filter;
    private boolean sticky;
    private FluidTank tank;
    private boolean needsFluidSync;

    public ConveyorTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<ConveyorTile>) ModuleTransportStorage.CONVEYOR.getBlock(), ModuleTransportStorage.CONVEYOR.type().get(), blockPos, blockState);
        this.facing = Direction.NORTH;
        this.type = ConveyorBlock.EnumType.FLAT;
        this.color = DyeColor.WHITE.getMapColor().col;
        this.filter = new ArrayList<>();
        this.sticky = false;
        this.tank = new FluidTank(250);
    }

    @Override
    public Level getBlockWorld() {
        return getLevel();
    }

    @Override
    public BlockPos getBlockPosition() {
        return getBlockPos();
    }

    @Override
    public void requestSync() {
        markForUpdate();
    }

    @Override
    public void requestFluidSync() {
        this.needsFluidSync = true;
    }

    @Override
    public boolean hasUpgrade(Direction facing) {
        return upgradeMap.containsKey(facing);
    }

    public int getPower() {
        int highestPower = 0;
        for (ConveyorUpgrade upgrade : upgradeMap.values()) {
            if (upgrade != null) {
                int power = upgrade.getRedstoneOutput();
                if (power > highestPower)
                    highestPower = power;
            }
        }
        return highestPower;
    }

    public void addUpgrade(Direction facing, ConveyorUpgradeFactory upgrade) {
        if (!hasUpgrade(facing)) {
            upgradeMap.put(facing, upgrade.create(this, facing));
            requestSync();
            if (level.isClientSide) this.getLevel().getModelDataManager().requestRefresh(this);
        }
    }

    @Override
    public void removeUpgrade(Direction facing, boolean drop) {
        if (hasUpgrade(facing)) {
            if (!level.isClientSide && drop) {
                ConveyorUpgrade upgrade = upgradeMap.get(facing);
                for (ItemStack stack : upgrade.getDrops()) {
                    ItemEntity item = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
                    item.setItem(stack);
                    level.addFreshEntity(item);
                }
            }
            upgradeMap.get(facing).onUpgradeRemoved();
            upgradeMap.remove(facing);
            requestSync();
            if (level.isClientSide) this.getLevel().getModelDataManager().requestRefresh(this);
        }
    }

    @Override
    public List<Integer> getEntityFilter() {
        return filter;
    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
        markForUpdate();
    }

    public EnumType getConveyorType() {
        return type;
    }

    public void setType(EnumType type) {
        this.type = type;
        markForUpdate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markForUpdate();
    }

    public void setColor(DyeColor color) {
        this.color = color.getMapColor().col;
        markForUpdate();
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
        markForUpdate();
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putString("Facing", facing.getSerializedName()); //getName
        compound.putString("Type", type.getName());
        compound.putInt("Color", color);
        compound.putBoolean("Sticky", sticky);
        CompoundTag upgrades = new CompoundTag();
        for (Direction facing : Direction.values()) {
            if (!hasUpgrade(facing)) {
                continue;
            }
            CompoundTag upgradeTag = new CompoundTag();
            ConveyorUpgrade upgrade = upgradeMap.get(facing);
            upgradeTag.putString("factory", BuiltInRegistries.ITEM.getKey(upgrade.getFactory().getUpgradeItem()).toString());
            CompoundTag customNBT = upgrade.serializeNBT(this.level.registryAccess());
            if (customNBT != null)
                upgradeTag.put("customNBT", customNBT);
            upgrades.put(facing.getSerializedName(), upgradeTag);
        }
        compound.put("Upgrades", upgrades);
        compound.put("Tank", tank.writeToNBT(provider, new CompoundTag()));
    }

    @Override //read
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        this.facing = Direction.byName(compound.getString("Facing"));
        this.type = ConveyorBlock.EnumType.getFromName(compound.getString("Type"));
        this.color = compound.getInt("Color");
        this.sticky = compound.getBoolean("Sticky");
        if (compound.contains("Upgrades")) {
            CompoundTag upgradesTag = compound.getCompound("Upgrades");
            //upgradeMap.clear();
            for (Direction facing : Direction.values()) {
                if (!upgradesTag.contains(facing.getSerializedName()))
                    continue;
                CompoundTag upgradeTag = upgradesTag.getCompound(facing.getSerializedName());
                ConveyorUpgradeFactory factory = null;
                for (ConveyorUpgradeFactory conveyorUpgradeFactory : ConveyorUpgradeFactory.FACTORIES) {
                    if (BuiltInRegistries.ITEM.getKey(conveyorUpgradeFactory.getUpgradeItem()).equals(ResourceLocation.parse(upgradeTag.getString("factory")))) {
                        factory = conveyorUpgradeFactory;
                        break;
                    }
                }
                if (factory != null) {
                    ConveyorUpgrade upgrade = upgradeMap.getOrDefault(facing, factory.create(this, facing));
                    if (upgradeTag.contains("customNBT")) {
                        upgrade.deserializeNBT(provider, upgradeTag.getCompound("customNBT"));
                        //upgradeMap.get(facing).deserializeNBT(upgradeTag.getCompound("customNBT"));
                    }
                    upgradeMap.put(facing, upgrade);
                }
            }
        }
        if (compound.contains("Tank")) {
            this.tank = this.tank.readFromNBT(provider, compound.getCompound("Tank"));
        }
    }

    public void markForUpdate() {
        super.markForUpdate();
        this.level.setBlockAndUpdate(worldPosition, this.level.getBlockState(worldPosition).setValue(FACING, facing).setValue(TYPE, type));
        CompoundTag compoundTag = new CompoundTag();
        saveAdditional(compoundTag, this.level.registryAccess());
        this.loadAdditional(compoundTag, this.level.registryAccess()); //read
    }

    public List<AABB> getCollisionBoxes() {
        List<AABB> boxes = new ArrayList<>();
        Direction facing = this.facing;
        if (type.isDown()) facing = facing.getOpposite();
        double height = 1;
        while (height > 0) {
            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                boxes.add(new AABB(0, 0, facing == Direction.NORTH ? 0 : 1D - height, 1, 1 - height, facing == Direction.NORTH ? height : 1));
            }
            if (facing == Direction.WEST || facing == Direction.EAST) {
                boxes.add(new AABB(facing == Direction.WEST ? 0 : 1D - height, 0, 0, facing == Direction.WEST ? height : 1, 1 - height, 1));
            }
            height -= 0.1D;
        }
        return boxes;
    }

    public void handleEntityMovement(Entity entity) {
        for (ConveyorUpgrade upgrade : upgradeMap.values()) {
            if (upgrade != null) {
                upgrade.handleEntity(entity);
            }
        }
        if (entity.isAlive()) {
            if (!this.getEntityFilter().contains(entity.getId()))
                MovementUtils.handleConveyorMovement(entity, facing, this.worldPosition, type);
            if (entity instanceof ItemEntity && sticky) ((ItemEntity) entity).setPickUpDelay(5);
        }
    }

    public Map<Direction, ConveyorUpgrade> getUpgradeMap() {
        return upgradeMap;
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ConveyorTile blockEntity) {
        if (!level.isClientSide && tank.getFluidAmount() > 0 && level.getGameTime() % 3 == 0 && level.getBlockState(this.worldPosition.relative(facing)).getBlock() instanceof ConveyorBlock && level.getBlockEntity(this.worldPosition.relative(facing)) instanceof ConveyorTile) {
            BlockState state1 = level.getBlockState(this.worldPosition.relative(facing));
            if (!state1.getValue(ConveyorBlock.TYPE).isVertical()) {
                int amount = Math.max(tank.getFluidAmount() - 1, 1);
                ConveyorTile conveyorTile = (ConveyorTile) level.getBlockEntity(this.worldPosition.relative(facing));
                FluidStack drained = tank.drain(conveyorTile.getTank().fill(tank.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty() && drained.getAmount() > 0) {
                    this.requestFluidSync();
                    conveyorTile.requestFluidSync();
                }
            }
        }
        if (!level.isClientSide && level.getGameTime() % 6 == 0 && needsFluidSync) {
            markForUpdate();
            this.needsFluidSync = false;
        }
        if (type.isVertical() && !upgradeMap.isEmpty()) {
            new ArrayList<>(upgradeMap.keySet()).forEach(facing1 -> this.removeUpgrade(facing1, true));
        }
        if (isServer() && this.level.getGameTime() % 20 == 0) {
            this.facing = this.level.getBlockState(this.worldPosition).getValue(FACING);
        }

        upgradeMap.values().forEach(ConveyorUpgrade::update);
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, ConveyorTile blockEntity) {

    }


    @Nonnull
    @Override
    public ConveyorTile getSelf() {
        return this;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player entityPlayer) {
        return new ContainerConveyor(menu, this, ((ConveyorBlock) ModuleTransportStorage.CONVEYOR.getBlock()).getFacingUpgradeHit(this.level.getBlockState(this.worldPosition), this.level, this.worldPosition, entityPlayer), inventoryPlayer);
    }

    public void openGui(Player player, Direction facing) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, packetBuffer -> {
                packetBuffer.writeBlockPos(worldPosition);
                packetBuffer.writeEnum(facing);
            });
        }
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ConveyorModelData.UPGRADE_PROPERTY, new ConveyorModelData(new HashMap<>(upgradeMap))).build();
    }

}
