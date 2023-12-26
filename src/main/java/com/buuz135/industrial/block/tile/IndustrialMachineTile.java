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

import com.buuz135.industrial.item.addon.ProcessingAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
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
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

public abstract class IndustrialMachineTile<T extends IndustrialMachineTile<T>> extends MachineTile<T> implements IRedstoneReader {

    @Save
    private TankInteractionBundle<IndustrialMachineTile> tankBundle;
    @Save
    private RedstoneManager<RedstoneAction> redstoneManager;
    private RedstoneControlButtonComponent<RedstoneAction> redstoneButton;
    private boolean tankBundleAdded;

    public IndustrialMachineTile(Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> basicTileBlock, BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<T>) basicTileBlock.getLeft().get(), basicTileBlock.getRight().get(), blockPos, blockState);
        this.redstoneManager = new RedstoneManager<>(RedstoneAction.IGNORE, false);
        this.tankBundleAdded = false;
        this.addButton(redstoneButton = new RedstoneControlButtonComponent<>(154, 84, 14, 14, () -> this.redstoneManager, () -> this));
    }

    @Override
    public void addTank(FluidTankComponent<T> tank) {
        super.addTank(tank);
        if (!tankBundleAdded) {
            this.addBundle(tankBundle = new TankInteractionBundle<>(() -> this.getCapability(ForgeCapabilities.FLUID_HANDLER), 175, 94, this, 10));
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
        return super.canAcceptAugment(augment);
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
        /*
        if (isSoulPowered() && level.random.nextDouble() < 0.3D){
            double posX = pos.getX();
            double posZ = pos.getZ();
            double offset = 0.1;
            if (level.random.nextBoolean()){
                posZ += level.random.nextDouble();
                if (level.random.nextBoolean()){
                    posX += 1 + offset;
                }else {
                    posX -= offset;
                }
            } else {
                posX += level.random.nextDouble();
                if (level.random.nextBoolean()){
                    posZ += 1 + offset;
                }else {
                    posZ -= offset;
                }
            }
            this.level.addParticle(ParticleTypes.SCULK_SOUL, posX, pos.getY()+0.1, posZ, 0,0.045,0);
        }*/
    }

    public boolean canBeSoulPowered() {
        return false;
    }

    public boolean isSoulPowered() {
        return true;
    }

}
