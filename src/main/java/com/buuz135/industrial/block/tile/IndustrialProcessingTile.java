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
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.state.BlockState;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class IndustrialProcessingTile<T extends IndustrialProcessingTile<T>> extends IndustrialMachineTile<T> {

    @Save
    private ProgressBarComponent<T> progressBar;

    public IndustrialProcessingTile(BasicTileBlock<T> basicTileBlock, int x, int y, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, blockPos, blockState);
        //this.addGuiAddonFactory(() -> new EnergyBarScreenAddon(10, 20, getEnergyStorage()));
        this.addProgressBar(progressBar = new ProgressBarComponent<T>(x, y, getMaxProgress()){
                    @Override
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon(x, y, progressBar){
                            @Override
                            public List<Component> getTooltipLines() {
                                List<Component> tooltip = new ArrayList<>();
                                tooltip.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.titanium.progressbar.progress").getString() +  ChatFormatting.WHITE + new DecimalFormat().format(progressBar.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(progressBar.getMaxProgress())));
                                int progress = (progressBar.getMaxProgress() - progressBar.getProgress());
                                if (!progressBar.getIncreaseType()) progress = progressBar.getMaxProgress() - progress;
                                tooltip.add(new TextComponent(ChatFormatting.GOLD + "ETA: " + ChatFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * progressBar.getTickingTime() / 20D / progressBar.getProgressIncrease())) + ChatFormatting.DARK_AQUA + "s"));
                                tooltip.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.industrialforegoing.usage").getString() +  ChatFormatting.WHITE + getTickPower() + ChatFormatting.DARK_AQUA+ " FE" + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE +ChatFormatting.DARK_AQUA+ "t"));
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
                        setCanIncrease(tileEntity -> getEnergyStorage().getEnergyStored() >= getTickPower() && canIncrease() && this.getRedstoneManager().getAction().canRun(tileEntity.getEnvironmentValue(false, null)) && this.getRedstoneManager().shouldWork()).
                        setOnTickWork(() -> {
                    getEnergyStorage().extractEnergy(getTickPower(), false);
                    progressBar.setProgressIncrease(this.hasAugmentInstalled(AugmentTypes.SPEED) ? (int) AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.SPEED).get(0), AugmentTypes.SPEED) : 1);
                }).
                setOnFinishWork(() -> {
                    int operations = (int) (this.hasAugmentInstalled(ProcessingAddonItem.PROCESSING) ? AugmentWrapper.getType(this.getInstalledAugments(ProcessingAddonItem.PROCESSING).get(0), ProcessingAddonItem.PROCESSING) : 1);
                    for (int i = 0; i < operations; i++) {
                        onFinish().run();
                    }
                    this.getRedstoneManager().finish();
                })
        );
    }

    public ProgressBarComponent<T> getProgressBar() {
        return progressBar;
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS)
            return InteractionResult.SUCCESS;
        openGui(playerIn);
        return InteractionResult.PASS;
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
