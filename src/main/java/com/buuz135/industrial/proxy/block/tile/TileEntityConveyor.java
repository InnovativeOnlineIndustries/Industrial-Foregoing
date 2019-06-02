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
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.module.ModuleTransport;
import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.buuz135.industrial.proxy.client.model.ConveyorModelData;
import com.buuz135.industrial.utils.MovementUtils;
import com.hrznstudio.titanium.block.tile.TileActive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.buuz135.industrial.proxy.block.BlockConveyor.*;

public class TileEntityConveyor extends TileActive implements IConveyorContainer, ITickable {

    private EnumFacing facing;
    private EnumType type;
    private int color;
    private Map<EnumFacing, ConveyorUpgrade> upgradeMap = new HashMap<>();
    private List<Integer> filter;
    private boolean sticky;
    private FluidTank tank;
    private boolean needsFluidSync;

    public TileEntityConveyor() {
        super(ModuleTransport.CONVEYOR);
        this.facing = EnumFacing.NORTH;
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
    public boolean hasUpgrade(EnumFacing facing) {
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

    public void addUpgrade(EnumFacing facing, ConveyorUpgradeFactory upgrade) {
        if (!hasUpgrade(facing)) {
            upgradeMap.put(facing, upgrade.create(this, facing));
            requestSync();
            if (world.isRemote) ModelDataManager.requestModelDataRefresh(this);
        }
    }

    @Override
    public void removeUpgrade(EnumFacing facing, boolean drop) {
        if (hasUpgrade(facing)) {
            if (!world.isRemote && drop) {
                ConveyorUpgrade upgrade = upgradeMap.get(facing);
                for (ItemStack stack : upgrade.getDrops()) {
                    EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    item.setItem(stack);
                    world.spawnEntity(item);
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

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
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

    public void setColor(EnumDyeColor color) {
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
    public NBTTagCompound write(NBTTagCompound compound) {
        compound = super.write(compound);
        compound.setString("Facing", facing.getName());
        compound.setString("Type", type.getName());
        compound.setInt("Color", color);
        compound.setBoolean("Sticky", sticky);
        NBTTagCompound upgrades = new NBTTagCompound();
        for (EnumFacing facing : EnumFacing.values()) {
            if (!hasUpgrade(facing))
                continue;
            NBTTagCompound upgradeTag = new NBTTagCompound();
            ConveyorUpgrade upgrade = upgradeMap.get(facing);
            upgradeTag.setString("factory", upgrade.getFactory().getRegistryName().toString());
            NBTTagCompound customNBT = upgrade.serializeNBT();
            if (customNBT != null)
                upgradeTag.setTag("customNBT", customNBT);
            upgrades.setTag(facing.getName(), upgradeTag);
        }
        compound.setTag("Upgrades", upgrades);
        compound.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);
        this.facing = EnumFacing.byName(compound.getString("Facing"));
        this.type = BlockConveyor.EnumType.getFromName(compound.getString("Type"));
        this.color = compound.getInt("Color");
        this.sticky = compound.getBoolean("Sticky");
        if (compound.contains("Upgrades", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound upgradesTag = compound.getCompound("Upgrades");
            //upgradeMap.clear();
            for (EnumFacing facing : EnumFacing.values()) {
                if (!upgradesTag.hasKey(facing.getName()))
                    continue;
                NBTTagCompound upgradeTag = upgradesTag.getCompound(facing.getName());
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
        if (compound.hasKey("Tank")) {
            this.tank = this.tank.readFromNBT(compound.getCompound("Tank"));
        }
    }

    public void markForUpdate() {
        super.markForUpdate();
        this.world.setBlockState(pos, this.world.getBlockState(pos).with(FACING, facing).with(TYPE, type));
        this.world.getTileEntity(pos).read(write(new NBTTagCompound()));
    }

    public List<AxisAlignedBB> getCollisionBoxes() {
        List<AxisAlignedBB> boxes = new ArrayList<>();
        EnumFacing facing = this.facing;
        if (type.isDown()) facing = facing.getOpposite();
        double height = 1;
        while (height > 0) {
            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                boxes.add(new AxisAlignedBB(0, 0, facing == EnumFacing.NORTH ? 0 : 1D - height, 1, 1 - height, facing == EnumFacing.NORTH ? height : 1));
            }
            if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
                boxes.add(new AxisAlignedBB(facing == EnumFacing.WEST ? 0 : 1D - height, 0, 0, facing == EnumFacing.WEST ? height : 1, 1 - height, 1));
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
            if (entity instanceof EntityItem && sticky) ((EntityItem) entity).setPickupDelay(5);
        }
    }

    public Map<EnumFacing, ConveyorUpgrade> getUpgradeMap() {
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
            IBlockState state = world.getBlockState(this.pos.offset(facing));
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
        if (world.isRemote() && world.getGameTime() % 20 * 20 * 60 == 0) { //TODO Remove this when forge fix
            ModelDataManager.requestModelDataRefresh(this);
        }
    }

    @Override
    public GuiScreen createGui(Container container) {
        return new GuiConveyor(container);
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryPlayer, EntityPlayer entityPlayer) {
        return new ContainerConveyor(this, ModuleTransport.CONVEYOR.getFacingUpgradeHit(this.world.getBlockState(this.pos), this.world, this.pos, entityPlayer), inventoryPlayer);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(ConveyorModelData.UPGRADE_PROPERTY, new ConveyorModelData(upgradeMap)).build();
    }

}