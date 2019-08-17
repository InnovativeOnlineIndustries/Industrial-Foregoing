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
package com.buuz135.industrial.proxy.block.tile;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.module.ModuleTransport;
import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.buuz135.industrial.proxy.client.model.ConveyorModelData;
import com.buuz135.industrial.utils.MovementUtils;
import com.hrznstudio.titanium.block.tile.TileActive;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.buuz135.industrial.proxy.block.BlockConveyor.*;

public class TileEntityConveyor extends TileActive implements IConveyorContainer, ITickableTileEntity {

    private Direction facing;
    private EnumType type;
    private int color;
    private Map<Direction, ConveyorUpgrade> upgradeMap = new HashMap<>();
    private List<Integer> filter;
    private boolean sticky;
    private FluidTank tank;
    private boolean needsFluidSync;

    public TileEntityConveyor() {
        super(ModuleTransport.CONVEYOR);
        this.facing = Direction.NORTH;
        this.type = BlockConveyor.EnumType.FLAT;
        this.color = 0;
        this.filter = new ArrayList<>();
        this.sticky = false;
        this.tank = new FluidTank(250);
    }

    @Override
    public World getConveyorWorld() {
        return getWorld();
    }

    @Override
    public BlockPos getConveyorPosition() {
        return getPos();
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
            if (world.isRemote) ModelDataManager.requestModelDataRefresh(this);
        }
    }

    @Override
    public void removeUpgrade(Direction facing, boolean drop) {
        if (hasUpgrade(facing)) {
            if (!world.isRemote && drop) {
                ConveyorUpgrade upgrade = upgradeMap.get(facing);
                for (ItemStack stack : upgrade.getDrops()) {
                    ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    item.setItem(stack);
                    world.addEntity(item);
                }
            }
            upgradeMap.get(facing).onUpgradeRemoved();
            upgradeMap.remove(facing);
            requestSync();
            if (world.isRemote) ModelDataManager.requestModelDataRefresh(this);
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
        this.color = 0;
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
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound.putString("Facing", facing.getName());
        compound.putString("Type", type.getName());
        compound.putInt("Color", color);
        compound.putBoolean("Sticky", sticky);
        CompoundNBT upgrades = new CompoundNBT();
        for (Direction facing : Direction.values()) {
            if (!hasUpgrade(facing)) {
                continue;
            }
            CompoundNBT upgradeTag = new CompoundNBT();
            ConveyorUpgrade upgrade = upgradeMap.get(facing);
            upgradeTag.putString("factory", upgrade.getFactory().getRegistryName().toString());
            CompoundNBT customNBT = upgrade.serializeNBT();
            if (customNBT != null)
                upgradeTag.put("customNBT", customNBT);
            upgrades.put(facing.getName(), upgradeTag);
        }
        compound.put("Upgrades", upgrades);
        compound.put("Tank", tank.writeToNBT(new CompoundNBT()));
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.facing = Direction.byName(compound.getString("Facing"));
        this.type = BlockConveyor.EnumType.getFromName(compound.getString("Type"));
        this.color = compound.getInt("Color");
        this.sticky = compound.getBoolean("Sticky");
        if (compound.contains("Upgrades")) {
            CompoundNBT upgradesTag = compound.getCompound("Upgrades");
            //upgradeMap.clear();
            for (Direction facing : Direction.values()) {
                if (!upgradesTag.contains(facing.getName()))
                    continue;
                CompoundNBT upgradeTag = upgradesTag.getCompound(facing.getName());
                ConveyorUpgradeFactory factory = null;
                for (ConveyorUpgradeFactory conveyorUpgradeFactory : ConveyorUpgradeFactory.FACTORIES) {
                    if (conveyorUpgradeFactory.getRegistryName().equals(new ResourceLocation(upgradeTag.getString("factory")))) {
                        factory = conveyorUpgradeFactory;
                        break;
                    }
                }
                if (factory != null) {
                    ConveyorUpgrade upgrade = upgradeMap.getOrDefault(facing, factory.create(this, facing));
                    if (upgradeTag.contains("customNBT", Constants.NBT.TAG_COMPOUND)) {
                        upgrade.deserializeNBT(upgradeTag.getCompound("customNBT"));
                        //upgradeMap.get(facing).deserializeNBT(upgradeTag.getCompound("customNBT"));
                    }
                    upgradeMap.put(facing, upgrade);
                }
            }
        }
        if (compound.contains("Tank")) {
            this.tank = this.tank.readFromNBT(compound.getCompound("Tank"));
        }
    }

    public void markForUpdate() {
        super.markForUpdate();
        this.world.setBlockState(pos, this.world.getBlockState(pos).with(FACING, facing).with(TYPE, type));
        this.world.getTileEntity(pos).read(write(new CompoundNBT()));
    }

    public List<AxisAlignedBB> getCollisionBoxes() {
        List<AxisAlignedBB> boxes = new ArrayList<>();
        Direction facing = this.facing;
        if (type.isDown()) facing = facing.getOpposite();
        double height = 1;
        while (height > 0) {
            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                boxes.add(new AxisAlignedBB(0, 0, facing == Direction.NORTH ? 0 : 1D - height, 1, 1 - height, facing == Direction.NORTH ? height : 1));
            }
            if (facing == Direction.WEST || facing == Direction.EAST) {
                boxes.add(new AxisAlignedBB(facing == Direction.WEST ? 0 : 1D - height, 0, 0, facing == Direction.WEST ? height : 1, 1 - height, 1));
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
            if (!this.getEntityFilter().contains(entity.getEntityId()))
                MovementUtils.handleConveyorMovement(entity, facing, this.pos, type);
            if (entity instanceof ItemEntity && sticky) ((ItemEntity) entity).setPickupDelay(5);
        }
    }

    public Map<Direction, ConveyorUpgrade> getUpgradeMap() {
        return upgradeMap;
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public void tick() {
        if (type.isVertical() && !upgradeMap.isEmpty()) {
            new ArrayList<>(upgradeMap.keySet()).forEach(facing1 -> this.removeUpgrade(facing1, true));
        }
        upgradeMap.values().forEach(ConveyorUpgrade::update);
        if (!world.isRemote && tank.getFluidAmount() > 0 && world.getGameTime() % 3 == 0 && world.getBlockState(this.pos.offset(facing)).getBlock() instanceof BlockConveyor && world.getTileEntity(this.pos.offset(facing)) instanceof TileEntityConveyor) {
            BlockState state = world.getBlockState(this.pos.offset(facing));
            if (!state.get(BlockConveyor.TYPE).isVertical()) {
                int amount = Math.max(tank.getFluidAmount() - 1, 1);
                TileEntityConveyor tileEntityConveyor = (TileEntityConveyor) world.getTileEntity(this.pos.offset(facing));
                FluidStack drained = tank.drain(tileEntityConveyor.getTank().fill(tank.drain(amount, false), true), true);
                if (drained != null && drained.amount > 0) {
                    this.requestFluidSync();
                    tileEntityConveyor.requestFluidSync();
                }
            }
        }
        if (!world.isRemote && world.getGameTime() % 6 == 0 && needsFluidSync) {
            markForUpdate();
            this.needsFluidSync = false;
        }
    }

    @Nullable
    @Override
    public Container createMenu(int menu, PlayerInventory inventoryPlayer, PlayerEntity entityPlayer) {
        return new ContainerConveyor(menu, this, ModuleTransport.CONVEYOR.getFacingUpgradeHit(this.world.getBlockState(this.pos), this.world, this.pos, entityPlayer), inventoryPlayer);
    }

    public void openGui(PlayerEntity player, Direction facing) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, packetBuffer -> {
                packetBuffer.writeBlockPos(pos);
                packetBuffer.writeEnumValue(facing);
            });
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(ConveyorModelData.UPGRADE_PROPERTY, new ConveyorModelData(new HashMap<>(upgradeMap))).build();
    }

}