package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.gui.transporter.ContainerTransporter;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.client.model.TransporterModelData;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransporterTile extends ActiveTile<TransporterTile> implements IBlockContainer<TransporterTypeFactory> {

    private Map<Direction, TransporterType> transporterTypeMap = new HashMap<>();

    public TransporterTile() {
        super(ModuleTransportStorage.TRANSPORTER);
    }

    @Nonnull
    @Override
    public TransporterTile getSelf() {
        return this;
    }

    @Override
    public World getBlockWorld() {
        return this.world;
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.pos;
    }

    @Override
    public void requestSync() {
        markForUpdate();
    }

    @Override
    public void requestFluidSync() {

    }

    @Override
    public void tick() {
        super.tick();
        if (isServer()) getTransporterTypeMap().values().forEach(TransporterType::update);
        if (isClient()) getTransporterTypeMap().values().forEach(TransporterType::updateClient);
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
            if (world.isRemote) ModelDataManager.requestModelDataRefresh(this);
        }
    }

    @Override
    public void removeUpgrade(Direction facing, boolean drop) {
        if (hasUpgrade(facing)) {
            if (!world.isRemote && drop) {
                TransporterType upgrade = transporterTypeMap.get(facing);
                for (ItemStack stack : upgrade.getDrops()) {
                    ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    item.setItem(stack);
                    world.addEntity(item);
                }
            }
            transporterTypeMap.get(facing).onUpgradeRemoved();
            transporterTypeMap.remove(facing);
            requestSync();
            if (world.isRemote) ModelDataManager.requestModelDataRefresh(this);
        }
    }

    public void openGui(PlayerEntity player, Direction facing) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, packetBuffer -> {
                packetBuffer.writeBlockPos(pos);
                packetBuffer.writeEnumValue(facing);
            });
        }
    }

    @Nullable
    @Override
    public Container createMenu(int menu, PlayerInventory inventoryPlayer, PlayerEntity entityPlayer) {
        return new ContainerTransporter(menu, this, ModuleTransportStorage.TRANSPORTER.getFacingUpgradeHit(this.world.getBlockState(this.pos), this.world, this.pos, entityPlayer).getLeft(), inventoryPlayer);
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
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        CompoundNBT upgrades = new CompoundNBT();
        for (Direction facing : Direction.values()) {
            if (!hasUpgrade(facing)) {
                continue;
            }
            CompoundNBT upgradeTag = new CompoundNBT();
            TransporterType upgrade = getTransporterTypeMap().get(facing);
            upgradeTag.putString("factory", upgrade.getFactory().getRegistryName().toString());
            CompoundNBT customNBT = upgrade.serializeNBT();
            if (customNBT != null)
                upgradeTag.put("customNBT", customNBT);
            upgrades.put(facing.getString(), upgradeTag);
        }
        compound.put("Transporters", upgrades);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        if (compound.contains("Transporters")) {
            CompoundNBT upgradesTag = compound.getCompound("Transporters");
            //upgradeMap.clear();
            for (Direction facing : Direction.values()) {
                if (!upgradesTag.contains(facing.getString()))
                    continue;
                CompoundNBT upgradeTag = upgradesTag.getCompound(facing.getString());
                TransporterTypeFactory factory = null;
                for (TransporterTypeFactory transporterTypeFactory : TransporterTypeFactory.FACTORIES) {
                    if (transporterTypeFactory.getRegistryName().equals(new ResourceLocation(upgradeTag.getString("factory")))) {
                        factory = transporterTypeFactory;
                        break;
                    }
                }
                if (factory != null) {
                    TransporterType upgrade = transporterTypeMap.getOrDefault(facing, factory.create(this, facing, TransporterTypeFactory.TransporterAction.EXTRACT));
                    if (upgradeTag.contains("customNBT", Constants.NBT.TAG_COMPOUND)) {
                        upgrade.deserializeNBT(upgradeTag.getCompound("customNBT"));
                        //upgradeMap.get(facing).deserializeNBT(upgradeTag.getCompound("customNBT"));
                    }
                    transporterTypeMap.put(facing, upgrade);
                }
            }
        }

    }
}
