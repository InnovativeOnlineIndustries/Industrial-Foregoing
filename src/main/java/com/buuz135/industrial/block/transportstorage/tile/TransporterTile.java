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
import com.buuz135.industrial.item.addon.ProcessingAddonItem;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.client.model.TransporterModelData;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.client.screen.addon.AssetScreenAddon;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TransporterTile extends ActiveTile<TransporterTile> implements IBlockContainer<TransporterTypeFactory> {

    @Save
    private SidedInventoryComponent<TransporterTile> augmentInventory;
    private Map<Direction, TransporterType> transporterTypeMap = new HashMap<>();

    public TransporterTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<TransporterTile>) ModuleTransportStorage.TRANSPORTER.getBlock(), ModuleTransportStorage.TRANSPORTER.type().get(), blockPos, blockState);
        addInventory(this.augmentInventory = (SidedInventoryComponent<TransporterTile>) getAugmentFactory()
                .create()
                .setComponentHarness(this.getSelf())
                .setInputFilter((stack, integer) -> AugmentWrapper.isAugment(stack) && canAcceptAugment(stack)));
        for (FacingUtil.Sideness value : FacingUtil.Sideness.values()) {
            augmentInventory.getFacingModes().put(value, IFacingComponent.FaceMode.NONE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(getAugmentBackground());
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
        var production = 1;
        if (this.hasAugmentInstalled(ProcessingAddonItem.PROCESSING)) {
            production += AugmentWrapper.getType(this.getInstalledAugments(ProcessingAddonItem.PROCESSING).getFirst(), ProcessingAddonItem.PROCESSING);
        }
        for (int i = 0; i < production; i++) {
            getTransporterTypeMap().values().forEach(TransporterType::update);
        }
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
            if (level.isClientSide) this.getLevel().getModelDataManager().requestRefresh(this);
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
            if (level.isClientSide) this.getLevel().getModelDataManager().requestRefresh(this);
        }
        if (transporterTypeMap.isEmpty()) {
            this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
        }
    }

    public void openGui(Player player, Direction facing) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, packetBuffer -> {
                packetBuffer.writeBlockPos(worldPosition);
                packetBuffer.writeEnum(facing);
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player entityPlayer) {
        return new ContainerTransporter(menu, this, ((TransporterBlock) ModuleTransportStorage.TRANSPORTER.getBlock()).getFacingUpgradeHit(this.level.getBlockState(this.worldPosition), this.level, this.worldPosition, entityPlayer).getLeft(), inventoryPlayer);
    }

    @Override
    public List<Integer> getEntityFilter() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(TransporterModelData.UPGRADE_PROPERTY, new TransporterModelData(new HashMap<>(transporterTypeMap))).build();
    }

    public Map<Direction, TransporterType> getTransporterTypeMap() {
        return transporterTypeMap;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        CompoundTag upgrades = new CompoundTag();
        for (Direction facing : Direction.values()) {
            if (!hasUpgrade(facing)) {
                continue;
            }
            CompoundTag upgradeTag = new CompoundTag();
            TransporterType upgrade = getTransporterTypeMap().get(facing);
            upgradeTag.putString("factory", BuiltInRegistries.ITEM.getKey(upgrade.getFactory().getUpgradeItem()).toString());
            CompoundTag customNBT = upgrade.serializeNBT(provider);
            if (customNBT != null)
                upgradeTag.put("customNBT", customNBT);
            upgrades.put(facing.getSerializedName(), upgradeTag);
        }
        compoundTag.put("Transporters", upgrades);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains("Transporters")) {
            CompoundTag upgradesTag = compound.getCompound("Transporters");
            //upgradeMap.clear();
            for (Direction facing : Direction.values()) {
                if (!upgradesTag.contains(facing.getSerializedName()))
                    continue;
                CompoundTag upgradeTag = upgradesTag.getCompound(facing.getSerializedName());
                TransporterTypeFactory factory = null;
                for (TransporterTypeFactory transporterTypeFactory : TransporterTypeFactory.FACTORIES) {
                    if (BuiltInRegistries.ITEM.getKey(transporterTypeFactory.getUpgradeItem()).equals(ResourceLocation.parse(upgradeTag.getString("factory")))) {
                        factory = transporterTypeFactory;
                        break;
                    }
                }
                if (factory != null) {
                    TransporterType upgrade = transporterTypeMap.getOrDefault(facing, factory.create(this, facing, TransporterTypeFactory.TransporterAction.EXTRACT));
                    if (upgradeTag.contains("customNBT")) {
                        upgrade.deserializeNBT(provider, upgradeTag.getCompound("customNBT"));
                        //upgradeMap.get(facing).deserializeNBT(upgradeTag.getCompound("customNBT"));
                    }
                    transporterTypeMap.put(facing, upgrade);
                }
            }
        }
    }

    public List<ItemStack> getInstalledAugments() {
        return getItemStackAugments().stream().filter(AugmentWrapper::isAugment).collect(Collectors.toList());
    }

    public List<ItemStack> getInstalledAugments(IAugmentType filter) {
        return getItemStackAugments().stream().filter(AugmentWrapper::isAugment).filter(stack -> AugmentWrapper.hasType(stack, filter)).collect(Collectors.toList());
    }

    public boolean hasAugmentInstalled(IAugmentType augmentType) {
        return getInstalledAugments(augmentType).size() > 0;
    }

    public IFactory<InventoryComponent<TransporterTile>> getAugmentFactory() {
        return () -> new SidedInventoryComponent<TransporterTile>("augments", 180, 11, 4, 0)
                .disableFacingAddon()
                .setColor(DyeColor.PURPLE)
                .setSlotLimit(1)
                .setRange(1, 4);
    }

    @OnlyIn(Dist.CLIENT)
    public IFactory<? extends IScreenAddon> getAugmentBackground() {
        return () -> new AssetScreenAddon(AssetTypes.AUGMENT_BACKGROUND, 175, 4, true);
    }

    private List<ItemStack> getItemStackAugments() {
        List<ItemStack> augments = new ArrayList<>();
        for (int i = 0; i < augmentInventory.getSlots(); i++) {
            augments.add(augmentInventory.getStackInSlot(i));
        }
        return augments;
    }

    public boolean canAcceptAugment(ItemStack augment) {
        if (AugmentWrapper.hasType(augment, AugmentTypes.SPEED)) {
            return !hasAugmentInstalled(AugmentTypes.SPEED);
        }
        if (AugmentWrapper.hasType(augment, AugmentTypes.EFFICIENCY)) {
            return !hasAugmentInstalled(AugmentTypes.EFFICIENCY);
        }
        if (AugmentWrapper.hasType(augment, ProcessingAddonItem.PROCESSING)) {
            return !hasAugmentInstalled(ProcessingAddonItem.PROCESSING);
        }
        return false;
    }

    public SidedInventoryComponent<TransporterTile> getAugmentInventory() {
        return augmentInventory;
    }
}
