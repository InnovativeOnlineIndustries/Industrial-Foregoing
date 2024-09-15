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

package com.buuz135.industrial.block.tile;

import com.buuz135.industrial.api.IMachineSettings;
import com.buuz135.industrial.item.addon.ProcessingAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.filter.IFilter;
import com.hrznstudio.titanium.api.redstone.IRedstoneReader;
import com.hrznstudio.titanium.api.redstone.IRedstoneState;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.redstone.RedstoneAction;
import com.hrznstudio.titanium.block.redstone.RedstoneManager;
import com.hrznstudio.titanium.block.redstone.RedstoneState;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.bundle.TankInteractionBundle;
import com.hrznstudio.titanium.component.button.RedstoneControlButtonComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Optional;

public abstract class IndustrialMachineTile<T extends IndustrialMachineTile<T>> extends MachineTile<T> implements IRedstoneReader, IMachineSettings {

    private static String settingsAddons = "MACHINE_ADDONS";
    private static String redstoneMode = "REDSTONE_MODE";
    private static String sidenessTank = "SIDENESS_TANK";
    private static String sidenessInventory = "SIDENESS_INVENTORY";
    private static String filter = "FILTER";

    @Save
    private TankInteractionBundle<IndustrialMachineTile> tankBundle;
    @Save
    private RedstoneManager<RedstoneAction> redstoneManager;
    @Save
    private String uuid = "d28b7061-fb92-4064-90fb-7e02b95a72a5";
    private RedstoneControlButtonComponent<RedstoneAction> redstoneButton;
    private boolean tankBundleAdded;

    public IndustrialMachineTile(BlockWithTile basicTileBlock, BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<T>) basicTileBlock.getBlock(), basicTileBlock.type().get(), blockPos, blockState);
        this.redstoneManager = new RedstoneManager<>(RedstoneAction.IGNORE, false);
        this.tankBundleAdded = false;
        this.addButton(redstoneButton = new RedstoneControlButtonComponent<>(154, 84, 14, 14, () -> this.redstoneManager, () -> this));
    }

    @Override
    public void addTank(FluidTankComponent<T> tank) {
        super.addTank(tank);
        if (!tankBundleAdded) {
            this.addBundle(tankBundle = new TankInteractionBundle<>(() -> Optional.of(this.getFluidHandler(null)), 175, 94, this, 10));
            this.tankBundleAdded = true;
        }
    }

    @Override
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
        if (AugmentWrapper.hasType(augment, RangeAddonItem.RANGE)) {
            return !hasAugmentInstalled(RangeAddonItem.RANGE);
        }
        return false;
    }

    public RedstoneManager<RedstoneAction> getRedstoneManager() {
        return redstoneManager;
    }

    @Override
    public IRedstoneState getEnvironmentValue(boolean strongPower, Direction direction) {
        if (strongPower) {
            if (direction == null) {
                return this.level.hasNeighborSignal(this.worldPosition) ? RedstoneState.ON : RedstoneState.OFF;
            }
            return this.level.hasSignal(this.worldPosition, direction) ? RedstoneState.ON : RedstoneState.OFF;
        } else {
            return this.level.getBestNeighborSignal(this.worldPosition) > 0 ? RedstoneState.ON : RedstoneState.OFF;
        }
    }

    @Override
    public void onNeighborChanged(Block blockIn, BlockPos fromPos) {
        super.onNeighborChanged(blockIn, fromPos);
        redstoneManager.setLastRedstoneState(this.getEnvironmentValue(false, null).isReceivingRedstone());
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return IndustrialAssetProvider.INSTANCE;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
    }

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        if (tag.contains(settingsAddons)) {
            var stacks = IMachineSettings.readInventory(this.level.registryAccess(), tag.getCompound(settingsAddons));
            for (var stack : stacks) {
                if (this.canAcceptAugment(stack)) {
                    for (ItemStack stackPlayer : player.inventoryMenu.getItems()) {
                        if (ItemStack.isSameItem(stack, stackPlayer)) {
                            var copiedStack = stackPlayer.copyWithCount(1);
                            if (ItemHandlerHelper.insertItem(this.getAugmentInventory(), copiedStack, false).isEmpty()) {
                                stackPlayer.shrink(1);
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (tag.contains(redstoneMode)) {
            NBTManager.getInstance().readTileEntity(this, this.level.registryAccess(), tag.getCompound(redstoneMode));
        }
        if (tag.contains(sidenessInventory)) {
            for (InventoryComponent<T> inventoryHandler : this.getMultiInventoryComponent().getInventoryHandlers()) {
                if (inventoryHandler instanceof SidedInventoryComponent<T> sided) {
                    if (tag.getCompound(sidenessInventory).contains(sided.getName())) {
                        CompoundTag intermediateTag = tag.getCompound(sidenessInventory).getCompound(sided.getName());
                        for (String allKey : intermediateTag.getAllKeys()) {
                            sided.getFacingModes().put(FacingUtil.Sideness.valueOf(allKey), IFacingComponent.FaceMode.valueOf(intermediateTag.getString(allKey)));
                        }
                    }
                }
            }
        }
        if (tag.contains(sidenessTank)) {
            for (FluidTankComponent<T> fluidTankComponent : this.getMultiTankComponent().getTanks()) {
                if (fluidTankComponent instanceof SidedFluidTankComponent<T> sided) {
                    if (tag.getCompound(sidenessTank).contains(sided.getName())) {
                        CompoundTag intermediateTag = tag.getCompound(sidenessTank).getCompound(sided.getName());
                        for (String allKey : intermediateTag.getAllKeys()) {
                            sided.getFacingModes().put(FacingUtil.Sideness.valueOf(allKey), IFacingComponent.FaceMode.valueOf(intermediateTag.getString(allKey)));
                        }
                    }
                }
            }
        }
        if (tag.contains(filter)) {
            for (IFilter iFilter : this.getMultiFilterComponent().getFilters()) {
                if (tag.getCompound(filter).contains(iFilter.getName())) {
                    iFilter.deserializeNBT(this.level.registryAccess(), tag.getCompound(filter).getCompound(iFilter.getName()));
                }
            }
        }
        markForUpdate();
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        tag.put(settingsAddons, IMachineSettings.writeInventory(this.level.registryAccess(), this.getAugmentInventory()));
        tag.put(redstoneMode, NBTManager.getInstance().writeTileEntityObject(this, redstoneManager, new CompoundTag()));
        if (this.getMultiInventoryComponent() != null) {
            CompoundTag sideInvTag = new CompoundTag();
            for (InventoryComponent<T> inventoryHandler : this.getMultiInventoryComponent().getInventoryHandlers()) {
                if (inventoryHandler instanceof SidedInventoryComponent<T> sided) {
                    CompoundTag intermediateTag = new CompoundTag();
                    for (FacingUtil.Sideness facing : sided.getFacingModes().keySet()) {
                        intermediateTag.putString(facing.name(), sided.getFacingModes().get(facing).name());
                    }
                    sideInvTag.put(sided.getName(), intermediateTag);
                }
            }
            tag.put(sidenessInventory, sideInvTag);
        }
        if (this.getMultiTankComponent() != null) {
            CompoundTag sideTankTag = new CompoundTag();
            for (FluidTankComponent<T> fluidTankComponent : this.getMultiTankComponent().getTanks()) {
                if (fluidTankComponent instanceof SidedFluidTankComponent<T> sided) {
                    CompoundTag intermediateTag = new CompoundTag();
                    for (FacingUtil.Sideness facing : sided.getFacingModes().keySet()) {
                        intermediateTag.putString(facing.name(), sided.getFacingModes().get(facing).name());
                    }
                    sideTankTag.put(sided.getName(), intermediateTag);
                }
            }
            tag.put(sidenessTank, sideTankTag);
        }
        if (this.getMultiFilterComponent() != null) {
            CompoundTag filterTag = new CompoundTag();
            for (IFilter iFilter : this.getMultiFilterComponent().getFilters()) {
                filterTag.put(iFilter.getName(), iFilter.serializeNBT(this.level.registryAccess()));
            }
            tag.put(filter, filterTag);
        }

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
