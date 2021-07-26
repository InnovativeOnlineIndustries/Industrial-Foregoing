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
package com.buuz135.industrial.block.generator.tile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class MycelialReactorTile extends IndustrialGeneratorTile<MycelialReactorTile> {

    @Save
    private String owner;
    private ProgressBarComponent<MycelialReactorTile> bar;

    public MycelialReactorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleGenerator.MYCELIAL_REACTOR, blockPos, blockState);
    }

    @Nonnull
    @Override
    public MycelialReactorTile getSelf() {
        return this;
    }

    @Override
    public int consumeFuel() {
        MycelialDataManager.getReactorAvailable(owner, this.level, true);
        return 5;
    }

    @Override
    public boolean canStart() {
        int amount = MycelialDataManager.getReactorAvailable(owner, this.level, false).size();
        if (amount == IMycelialGeneratorType.TYPES.size()){
            MycelialDataManager.getReactorAvailable(owner, this.level, true);
            return true;
        }
        markForUpdate();
        return false;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 25_000_000;
    }

    @Override
    public ProgressBarComponent<MycelialReactorTile> getProgressBar() {
        bar = new ProgressBarComponent<MycelialReactorTile>(30, 20, 0, 100){
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon(30, 20, bar){
                    @Override
                    public List<Component> getTooltipLines() {
                        List<Component> tooltip = new ArrayList<>();
                        tooltip.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.titanium.progressbar.progress").getString() +  ChatFormatting.WHITE + new DecimalFormat().format(bar.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(bar.getMaxProgress())));
                        int progress = (bar.getMaxProgress() - bar.getProgress());
                        if (!bar.getIncreaseType()) progress = bar.getMaxProgress() - progress;
                        tooltip.add(new TextComponent(ChatFormatting.GOLD + "ETA: " + ChatFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * bar.getTickingTime() / 20D / bar.getProgressIncrease())) + ChatFormatting.DARK_AQUA + "s"));
                        tooltip.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.industrialforegoing.generating").getString() +  ChatFormatting.WHITE + new DecimalFormat().format(getEnergyProducedEveryTick()) + ChatFormatting.DARK_AQUA+ " FE" + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE +ChatFormatting.DARK_AQUA+ "t"));
                        return tooltip;
                    }
                });
            }
        }
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
        return bar;
    }

    @Override
    public int getEnergyCapacity() {
        return 100_000_000;
    }

    @Override
    public int getExtractingEnergy() {
        return getEnergyCapacity();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ProgressBarComponent<MycelialReactorTile> getBar() {
        return bar;
    }
}
