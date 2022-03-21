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
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.TransporterBlock;
import com.buuz135.industrial.gui.transporter.ContainerTransporter;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.client.model.TransporterModelData;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransporterTile extends ActiveTile<TransporterTile> implements IBlockContainer<TransporterTypeFactory> {

    private Map<Direction, TransporterType> transporterTypeMap = new HashMap<>();

    public TransporterTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<TransporterTile>) ModuleTransportStorage.TRANSPORTER.getLeft().get(), ModuleTransportStorage.TRANSPORTER.getRight().get(), blockPos, blockState);
    }

    @Nonnull
    @Override
    public TransporterTile getSelf() {
        return this;
    }

    @Override
    public Level getBlockWorld() {
        return this.level;
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.worldPosition;
    }

    @Override
    public void requestSync() {
        markForUpdate();
    }

    @Override
    public void requestFluidSync() {

    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, TransporterTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        getTransporterTypeMap().values().forEach(TransporterType::update);
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, TransporterTile blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
        getTransporterTypeMap().values().forEach(TransporterType::updateClient);
    }

    @Override
    public boolean hasUpgrade(Direction facing) {
        return transporterTypeMap.containsKey(facing);
    }

    @Override
    public void addUpgrade(Direction facing, TransporterTypeFactory factory) {
        if (!hasUpgrade(facing)) {
            transporterTypeMap.put(facing, factory.create(this, facing, TransporterTypeFactory.TransporterAction.EXTRACT));
            requestSync();
            if (level.isClientSide) ModelDataManager.requestModelDataRefresh(this);
        }
    }

    @Override
    public void removeUpgrade(Direction facing, boolean drop) {
        if (hasUpgrade(facing)) {
            if (!level.isClientSide && drop) {
                TransporterType upgrade = transporterTypeMap.get(facing);
                for (ItemStack stack : upgrade.getDrops()) {
                    ItemEntity item = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
                    item.setItem(stack);
                    level.addFreshEntity(item);
                }
            }
            transporterTypeMap.get(facing).onUpgradeRemoved();
            transporterTypeMap.remove(facing);
            requestSync();
            if (level.isClientSide) ModelDataManager.requestModelDataRefresh(this);
        }
        if (transporterTypeMap.isEmpty()) {
            this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
        }
    }

    public void openGui(Player player, Direction facing) {
        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui((ServerPlayer) player, this, packetBuffer -> {
                packetBuffer.writeBlockPos(worldPosition);
                packetBuffer.writeEnum(facing);
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player entityPlayer) {
        return new ContainerTransporter(menu, this, ((TransporterBlock)ModuleTransportStorage.TRANSPORTER.getLeft().get()).getFacingUpgradeHit(this.level.getBlockState(this.worldPosition), this.level, this.worldPosition, entityPlayer).getLeft(), inventoryPlayer);
    }

    @Override
    public List<Integer> getEntityFilter() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(TransporterModelData.UPGRADE_PROPERTY, new TransporterModelData(new HashMap<>(transporterTypeMap))).build();
    }

    public Map<Direction, TransporterType> getTransporterTypeMap() {
        return transporterTypeMap;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        CompoundTag upgrades = new CompoundTag();
        for (Direction facing : Direction.values()) {
            if (!hasUpgrade(facing)) {
                continue;
            }
            CompoundTag upgradeTag = new CompoundTag();
            TransporterType upgrade = getTransporterTypeMap().get(facing);
            upgradeTag.putString("factory", upgrade.getFactory().getRegistryName().toString());
            CompoundTag customNBT = upgrade.serializeNBT();
            if (customNBT != null)
                upgradeTag.put("customNBT", customNBT);
            upgrades.put(facing.getSerializedName(), upgradeTag);
        }
        compoundTag.put("Transporters", upgrades);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("Transporters")) {
            CompoundTag upgradesTag = compound.getCompound("Transporters");
            //upgradeMap.clear();
            for (Direction facing : Direction.values()) {
                if (!upgradesTag.contains(facing.getSerializedName()))
                    continue;
                CompoundTag upgradeTag = upgradesTag.getCompound(facing.getSerializedName());
                TransporterTypeFactory factory = null;
                for (TransporterTypeFactory transporterTypeFactory : TransporterTypeFactory.FACTORIES) {
                    if (transporterTypeFactory.getRegistryName().equals(new ResourceLocation(upgradeTag.getString("factory")))) {
                        factory = transporterTypeFactory;
                        break;
                    }
                }
                if (factory != null) {
                    TransporterType upgrade = transporterTypeMap.getOrDefault(facing, factory.create(this, facing, TransporterTypeFactory.TransporterAction.EXTRACT));
                    if (upgradeTag.contains("customNBT")) {
                        upgrade.deserializeNBT(upgradeTag.getCompound("customNBT"));
                        //upgradeMap.get(facing).deserializeNBT(upgradeTag.getCompound("customNBT"));
                    }
                    transporterTypeMap.put(facing, upgrade);
                }
            }
        }
    }
}
