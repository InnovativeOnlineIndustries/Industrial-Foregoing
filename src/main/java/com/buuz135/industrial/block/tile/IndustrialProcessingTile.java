package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public abstract class IndustrialProcessingTile<T extends IndustrialProcessingTile<T>> extends MachineTile<T> {

    @Save
    private ProgressBarComponent<T> progressBar;

    public IndustrialProcessingTile(BasicTileBlock<T> basicTileBlock, int x, int y) {
        super(basicTileBlock);
        this.addGuiAddonFactory(() -> new EnergyBarScreenAddon(10, 20, getEnergyStorage()));
        this.addProgressBar(progressBar = new ProgressBarComponent<T>(x, y, 100).
                setComponentHarness(this.getSelf()).
                setBarDirection(ProgressBarComponent.BarDirection.HORIZONTAL_RIGHT).
                setCanReset(tileEntity -> true).
                setOnStart(() -> progressBar.setMaxProgress(getMaxProgress())).
                setCanIncrease(tileEntity -> getEnergyStorage().getEnergyStored() > getTickPower() && canIncrease()).
                setOnTickWork(() -> getEnergyStorage().extractEnergyForced(getTickPower())).
                setOnFinishWork(() -> onFinish().run())
        );
    }

    public ProgressBarComponent<T> getProgressBar() {
        return progressBar;
    }

    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ActionResultType.SUCCESS)
            return ActionResultType.SUCCESS;
        openGui(playerIn);
        return ActionResultType.PASS;
    }

    public int getMaxProgress() {
        return 100;
    }

    public abstract boolean canIncrease();

    public abstract Runnable onFinish();

    protected abstract int getTickPower();
}
