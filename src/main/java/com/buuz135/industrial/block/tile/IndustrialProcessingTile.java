package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.TileMachine;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import com.hrznstudio.titanium.client.gui.addon.EnergyBarGuiAddon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public abstract class IndustrialProcessingTile extends TileMachine {

    @Save
    private PosProgressBar progressBar;

    public IndustrialProcessingTile(BlockTileBase blockTileBase, int x, int y) {
        super(blockTileBase);
        this.addGuiAddonFactory(() -> new EnergyBarGuiAddon(10, 20, getEnergyStorage()));
        this.addProgressBar(progressBar = new PosProgressBar(x, y, 100).
                setTile(this).
                setBarDirection(PosProgressBar.BarDirection.HORIZONTAL_RIGHT).
                setCanReset(tileEntity -> true).
                setOnStart(() -> progressBar.setMaxProgress(getMaxProgress())).
                setCanIncrease(tileEntity -> getEnergyStorage().getEnergyStored() > getTickPower() && canIncrease()).
                setOnTickWork(() -> getEnergyStorage().extractEnergyForced(getTickPower())).
                setOnFinishWork(() -> onFinish().run())
        );
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

    public int getMaxProgress() {
        return 100;
    }

    public abstract boolean canIncrease();

    public abstract Runnable onFinish();

    protected abstract int getTickPower();
}
