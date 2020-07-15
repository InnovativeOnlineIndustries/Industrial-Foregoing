package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.component.bundle.TankInteractionBundle;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public abstract class IndustrialMachineTile<T extends IndustrialMachineTile<T>> extends MachineTile<T> {

    @Save
    private TankInteractionBundle<IndustrialMachineTile> tankBundle;

    public IndustrialMachineTile(BasicTileBlock<T> basicTileBlock) {
        super(basicTileBlock);
        this.addBundle(tankBundle = new TankInteractionBundle<>(() -> this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY), 175, 94, this, 10));
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
}
