package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class IndustrialProcessingTile<T extends IndustrialProcessingTile<T>> extends IndustrialMachineTile<T> {

    @Save
    private ProgressBarComponent<T> progressBar;

    public IndustrialProcessingTile(BasicTileBlock<T> basicTileBlock, int x, int y) {
        super(basicTileBlock);
        //this.addGuiAddonFactory(() -> new EnergyBarScreenAddon(10, 20, getEnergyStorage()));
        this.addProgressBar(progressBar = new ProgressBarComponent<T>(x, y, 100){
                    @Override
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon(x, y, progressBar){
                            @Override
                            public List<ITextComponent> getTooltipLines() {
                                List<ITextComponent> tooltip = new ArrayList<>();
                                tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.titanium.progressbar.progress").getString() +  TextFormatting.WHITE + new DecimalFormat().format(progressBar.getProgress()) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(progressBar.getMaxProgress())));
                                int progress = (progressBar.getMaxProgress() - progressBar.getProgress());
                                if (!progressBar.getIncreaseType()) progress = progressBar.getMaxProgress() - progress;
                                tooltip.add(new StringTextComponent(TextFormatting.GOLD + "ETA: " + TextFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * progressBar.getTickingTime() / 20D / progressBar.getProgressIncrease())) + TextFormatting.DARK_AQUA + "s"));
                                tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.industrialforegoing.usage").getString() +  TextFormatting.WHITE + getTickPower() + TextFormatting.DARK_AQUA+ " FE" + TextFormatting.GOLD + "/" + TextFormatting.WHITE +TextFormatting.DARK_AQUA+ "t"));
                                return tooltip;
                            }
                        });
                    }
                }.
                setComponentHarness(this.getSelf()).
                setBarDirection(getBarDirection()).
                setCanReset(tileEntity -> true).
                setOnStart(() -> {
                    int maxProgress = (int) Math.floor(getMaxProgress() * (this.hasAugmentInstalled(AugmentTypes.EFFICIENCY) ? AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.EFFICIENCY).get(0), AugmentTypes.EFFICIENCY) : 1));
                    progressBar.setMaxProgress(maxProgress);
                }).
                setCanIncrease(tileEntity -> getEnergyStorage().getEnergyStored() > getTickPower() && canIncrease() && this.getRedstoneManager().getAction().canRun(tileEntity.getEnvironmentValue(false, null)) && this.getRedstoneManager().shouldWork()).
                setOnTickWork(() -> {
                    getEnergyStorage().extractEnergy(getTickPower(), false);
                    progressBar.setProgressIncrease(this.hasAugmentInstalled(AugmentTypes.SPEED) ? (int) AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.SPEED).get(0), AugmentTypes.SPEED) : 1);
                }).
                setOnFinishWork(() -> {
                    onFinish().run();
                    this.getRedstoneManager().finish();
                })
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

    public ProgressBarComponent.BarDirection getBarDirection() {
        return ProgressBarComponent.BarDirection.ARROW_RIGHT;
    }
}
