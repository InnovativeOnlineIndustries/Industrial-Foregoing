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

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MycelialReactorTile extends IndustrialGeneratorTile<MycelialReactorTile> {

    public static final HashMap<Level, List<BlockPos>> REACTOR_POSITIONS = new HashMap<>();

    static {
        EventManager.forge(LevelTickEvent.Post.class).process(event -> {
            for (BlockPos reactorPosition : REACTOR_POSITIONS.getOrDefault(event.getLevel(), new ArrayList<>())) {
                var tile = event.getLevel().getBlockEntity(reactorPosition);
                if (tile instanceof MycelialReactorTile reactorTile) {
                    int amount = MycelialDataManager.getReactorAvailable(reactorTile.owner, tile.getLevel(), false).size();
                    if (amount == IMycelialGeneratorType.TYPES.size()) {
                        MycelialDataManager.getReactorAvailable(reactorTile.owner, tile.getLevel(), true);
                        reactorTile.setCanWork(true);
                    }
                }
            }
            REACTOR_POSITIONS.getOrDefault(event.getLevel(), new ArrayList<>()).clear();
        }).subscribe();
    }

    @Save
    private String owner;
    private ProgressBarComponent<MycelialReactorTile> bar;
    private boolean canWork;

    public MycelialReactorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleGenerator.MYCELIAL_REACTOR, blockPos, blockState);
        this.canWork = false;
    }

    @Nonnull
    @Override
    public MycelialReactorTile getSelf() {
        return this;
    }

    @Override
    public int consumeFuel() {
        this.canWork = false;
        markForUpdate();
        return 5;
    }

    @Override
    public boolean canStart() {
        if (this.canWork)
            return true;
        markForUpdate();
        return false;
    }

    public void setCanWork(boolean canWork) {
        this.canWork = canWork;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, MycelialReactorTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (!REACTOR_POSITIONS.getOrDefault(this.level, new ArrayList<>()).contains(this.worldPosition)) {
            REACTOR_POSITIONS.computeIfAbsent(this.level, (a) -> new ArrayList<>()).add(this.worldPosition);
        }
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 25_000_000;
    }

    @Override
    public ProgressBarComponent<MycelialReactorTile> getProgressBar() {
        bar = new ProgressBarComponent<MycelialReactorTile>(30, 20, 0, 100) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon(30, 20, bar) {
                    @Override
                    @OnlyIn(Dist.CLIENT)
                    public List<Component> getTooltipLines() {
                        List<Component> tooltip = new ArrayList<>();
                        tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.titanium.progressbar.progress").getString() + ChatFormatting.WHITE + new DecimalFormat().format(bar.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(bar.getMaxProgress())));
                        int progress = (bar.getMaxProgress() - bar.getProgress());
                        if (!bar.getIncreaseType()) progress = bar.getMaxProgress() - progress;
                        tooltip.add(Component.literal(ChatFormatting.GOLD + "ETA: " + ChatFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * bar.getTickingTime() / 20D / bar.getProgressIncrease())) + ChatFormatting.DARK_AQUA + "s"));
                        tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.generating").getString() + ChatFormatting.WHITE + new DecimalFormat().format(getEnergyProducedEveryTick()) + ChatFormatting.DARK_AQUA + " FE" + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + ChatFormatting.DARK_AQUA + "t"));
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
