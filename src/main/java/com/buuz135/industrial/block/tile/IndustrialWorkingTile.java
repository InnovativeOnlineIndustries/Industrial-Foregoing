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
import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class IndustrialWorkingTile<T extends IndustrialWorkingTile<T>> extends IndustrialMachineTile<T> {

    @Save
    private ProgressBarComponent<T> workingBar;

    public IndustrialWorkingTile(Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> basicTileBlock, int estimatedPower, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, blockPos, blockState);
        this.addProgressBar(workingBar = new ProgressBarComponent<T>(30, 20, getMaxProgress(), getMaxProgress()) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon(30, 20, workingBar) {
                    @Override
                    public List<Component> getTooltipLines() {
                        List<Component> tooltip = new ArrayList<>();
                        tooltip.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.titanium.progressbar.progress").getString() + ChatFormatting.WHITE + new DecimalFormat().format(workingBar.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(workingBar.getMaxProgress())));
                        int progress = (workingBar.getMaxProgress() - workingBar.getProgress());
                        if (!workingBar.getIncreaseType()) progress = workingBar.getMaxProgress() - progress;
                        tooltip.add(new TextComponent(ChatFormatting.GOLD + "ETA: " + ChatFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * workingBar.getTickingTime() / 20D / workingBar.getProgressIncrease())) + ChatFormatting.DARK_AQUA + "s"));
                        if (estimatedPower > 0) tooltip.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.industrialforegoing.usage").getString() + ChatFormatting.WHITE + estimatedPower + ChatFormatting.DARK_AQUA + " FE"));
                        return tooltip;
                    }
                });
            }
        }
                .setComponentHarness(this.getSelf())
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setIncreaseType(false)
                .setOnFinishWork(() -> {
                    if (isServer()) {
                        WorkAction work = work();
                        this.getEnergyStorage().extractEnergy(work.getEnergyConsumed(), false);
                        int operations = (int) (this.hasAugmentInstalled(ProcessingAddonItem.PROCESSING) ? AugmentWrapper.getType(this.getInstalledAugments(ProcessingAddonItem.PROCESSING).get(0), ProcessingAddonItem.PROCESSING) - 1 : 0);
                        for (int i = 0; i < operations; i++) {
                            work = work();
                            this.getEnergyStorage().extractEnergy(work.getEnergyConsumed(), false);
                        }
                        int maxProgress = (int) Math.floor(getMaxProgress() * (this.hasAugmentInstalled(AugmentTypes.EFFICIENCY) ? AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.EFFICIENCY).get(0), AugmentTypes.EFFICIENCY) : 1));
                        workingBar.setMaxProgress(maxProgress);
                        workingBar.setProgress((int) (maxProgress * work.getWorkAmount()));
                        this.getRedstoneManager().finish();
                    }
                })
                .setOnTickWork(() -> {
                    workingBar.setProgressIncrease(this.hasAugmentInstalled(AugmentTypes.SPEED) ? (int) AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.SPEED).get(0), AugmentTypes.SPEED) : 1);
                })
                .setCanReset(tileEntity -> true)
                .setCanIncrease(tileEntity -> this.getRedstoneManager().getAction().canRun(tileEntity.getEnvironmentValue(false, null)) && this.getRedstoneManager().shouldWork())
                .setColor(DyeColor.LIME));
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS)
            return InteractionResult.SUCCESS;
        openGui(playerIn);
        return InteractionResult.PASS;
    }

    public abstract WorkAction work();

    public boolean hasEnergy(int amount) {
        return this.getEnergyStorage().getEnergyStored() >= amount;
    }

    public int getMaxProgress() {
        return 100;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return IndustrialAssetProvider.INSTANCE;
    }

    @Override
    public boolean canAcceptAugment(ItemStack augment) {
        if (AugmentWrapper.hasType(augment, ProcessingAddonItem.PROCESSING))
            return !hasAugmentInstalled(ProcessingAddonItem.PROCESSING);
        return super.canAcceptAugment(augment);
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
