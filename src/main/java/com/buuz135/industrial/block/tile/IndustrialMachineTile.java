package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.redstone.IRedstoneReader;
import com.hrznstudio.titanium.api.redstone.IRedstoneState;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.redstone.RedstoneAction;
import com.hrznstudio.titanium.block.redstone.RedstoneManager;
import com.hrznstudio.titanium.block.redstone.RedstoneState;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.component.bundle.TankInteractionBundle;
import com.hrznstudio.titanium.component.button.RedstoneControlButtonComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public abstract class IndustrialMachineTile<T extends IndustrialMachineTile<T>> extends MachineTile<T> implements IRedstoneReader {

    @Save
    private TankInteractionBundle<IndustrialMachineTile> tankBundle;
    @Save
    private RedstoneManager<RedstoneAction> redstoneManager;
    private RedstoneControlButtonComponent<RedstoneAction> redstoneButton;
    private boolean tankBundleAdded;

    public IndustrialMachineTile(BasicTileBlock<T> basicTileBlock) {
        super(basicTileBlock);
        this.redstoneManager = new RedstoneManager<>(RedstoneAction.IGNORE, false);
        this.tankBundleAdded = false;
        this.addButton(redstoneButton = new RedstoneControlButtonComponent<>(154, 84, 14, 14, () -> this.redstoneManager, () -> this));
    }

    @Override
    public void addTank(FluidTankComponent<T> tank) {
        super.addTank(tank);
        if (!tankBundleAdded) {
            this.addBundle(tankBundle = new TankInteractionBundle<>(() -> this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY), 175, 94, this, 10));
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
        return super.canAcceptAugment(augment);
    }

    public RedstoneManager<RedstoneAction> getRedstoneManager() {
        return redstoneManager;
    }

    @Override
    public IRedstoneState getEnvironmentValue(boolean strongPower, Direction direction) {
        if (strongPower) {
            if (direction == null) {
                return this.world.isBlockPowered(this.pos) ? RedstoneState.ON : RedstoneState.OFF;
            }
            return this.world.isSidePowered(this.pos, direction) ? RedstoneState.ON : RedstoneState.OFF;
        } else {
            return this.world.getRedstonePowerFromNeighbors(this.pos) > 0 ? RedstoneState.ON : RedstoneState.OFF;
        }
    }

    @Override
    public void onNeighborChanged(Block blockIn, BlockPos fromPos) {
        super.onNeighborChanged(blockIn, fromPos);
        redstoneManager.setLastRedstoneState(this.getEnvironmentValue(false, null).isReceivingRedstone());
    }
}
