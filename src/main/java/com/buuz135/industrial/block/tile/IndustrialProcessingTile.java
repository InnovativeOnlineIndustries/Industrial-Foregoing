package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.TilePowered;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import com.hrznstudio.titanium.client.gui.addon.EnergyBarGuiAddon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public abstract class IndustrialProcessingTile extends TilePowered {

    @Save
    private PosProgressBar progressBar;
    @Save
    private SidedInvHandler addons;

    public IndustrialProcessingTile(BlockTileBase blockTileBase, int x, int y, int maxProgress) {
        super(blockTileBase);
        this.addGuiAddonFactory(() -> new EnergyBarGuiAddon(10, 20, getEnergyStorage()));
        this.addProgressBar(progressBar = new PosProgressBar(x, y, maxProgress).
                setTile(this).
                setBarDirection(PosProgressBar.BarDirection.HORIZONTAL_RIGHT).
                setCanReset(tileEntity -> true).
                setCanIncrease(tileEntity -> getEnergyStorage().getEnergyStored() > getTickPower() && canIncrease()).
                setOnTickWork(() -> getEnergyStorage().extractEnergyForced(getTickPower())).
                setOnFinishWork(() -> onFinish().run())
        );
        this.addInventory(addons = (SidedInvHandler) new SidedInvHandler("addons", 176 - 24, 8, 4, 0)
                .setColor(DyeColor.CYAN)
                .setTile(this)
                .setRange(1, 4)
                .setSlotLimit(1));
    }

    public PosProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public boolean onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ)) return true;
        openGui(playerIn);
        return true;
    }

    public abstract boolean canIncrease();

    public abstract Runnable onFinish();

    protected abstract int getTickPower();
}
