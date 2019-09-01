package com.buuz135.industrial.block.tile;

import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.TilePowered;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import com.hrznstudio.titanium.client.gui.addon.EnergyBarGuiAddon;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public abstract class IndustrialWorkingTile extends TilePowered {

    @Save
    private PosProgressBar workingBar;
    @Save
    private SidedInvHandler addons;

    public IndustrialWorkingTile(BlockTileBase blockTileBase, int maxProgress) {
        super(blockTileBase);
        this.addGuiAddonFactory(() -> new EnergyBarGuiAddon(10, 20, getEnergyStorage()));
        this.addProgressBar(workingBar = new PosProgressBar(30, 20, 0, maxProgress)
                .setTile(this)
                .setBarDirection(PosProgressBar.BarDirection.VERTICAL_UP)
                .setIncreaseType(false)
                .setOnFinishWork(() -> {
                    if (isServer()) {
                        WorkAction work = work();
                        workingBar.setProgress((int) (workingBar.getMaxProgress() * work.getWorkAmount()));
                        this.getEnergyStorage().extractEnergyForced(work.getEnergyConsumed());
                    }
                })
                .setCanReset(tileEntity -> true)
                .setCanIncrease(tileEntity -> true)
                .setColor(DyeColor.LIME));
        this.addInventory(addons = (SidedInvHandler) new SidedInvHandler("addons", 176 - 24, 8, 4, 1)
                .setColor(DyeColor.CYAN)
                .setTile(this)
                .setRange(1, 4)
                .setSlotLimit(1));
    }

    @Override
    public boolean onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (!super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ)) {
            openGui(playerIn);
            return true;
        }
        return false;
    }

    public abstract WorkAction work();

    public boolean hasEnergy(int amount) {
        return this.getEnergyStorage().getEnergyStored() >= amount;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return IndustrialAssetProvider.INSTANCE;
    }

    public class WorkAction {
        private final float workAmount;
        private final int energyConsumed;

        public WorkAction(float workAmount, int energyConsumed) {
            this.workAmount = workAmount;
            this.energyConsumed = energyConsumed;
        }

        public float getWorkAmount() {
            return workAmount;
        }

        public int getEnergyConsumed() {
            return energyConsumed;
        }
    }
}
