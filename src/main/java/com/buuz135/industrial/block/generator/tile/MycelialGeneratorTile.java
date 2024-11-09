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
import com.buuz135.industrial.gui.component.GeneratorBackgroundScreenAddon;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.module.BlockWithTile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MycelialGeneratorTile extends IndustrialGeneratorTile<MycelialGeneratorTile> {

    @Save
    private int powerGeneration;
    private IMycelialGeneratorType type;
    private INBTSerializable<CompoundTag>[] inputs;
    private ProgressBarComponent<MycelialGeneratorTile> bar;
    @Save
    private String owner;

    public MycelialGeneratorTile(BlockWithTile basicTileBlock, IMycelialGeneratorType type, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, blockPos, blockState);
        this.type = type;
        this.powerGeneration = 10;
        this.inputs = new INBTSerializable[this.type.getInputs().length];
        for (int i = 0; i < this.type.getInputs().length; i++) {
            if (this.type.getInputs()[i] == IMycelialGeneratorType.Input.SLOT) {
                SidedInventoryComponent<MycelialGeneratorTile> slot = (SidedInventoryComponent<MycelialGeneratorTile>) new SidedInventoryComponent<MycelialGeneratorTile>(this.type.getName() + ".input_" + i, 44 + i * 21, 22, 1, i)
                        .setColor(this.type.getInputColors()[i])
                        .setInputFilter(this.type.getSlotInputPredicates().get(i))
                        .setSlotLimit(type.getSlotSize());
                this.addInventory(slot);
                this.inputs[i] = slot;
            } else if (this.type.getInputs()[i] == IMycelialGeneratorType.Input.TANK) {
                SidedFluidTankComponent<MycelialGeneratorTile> slot = (SidedFluidTankComponent<MycelialGeneratorTile>) new SidedFluidTankComponent<MycelialGeneratorTile>(this.type.getName() + ".input_" + i, 8000, 44 + i * 21, 20, i)
                        .setColor(this.type.getInputColors()[i])
                        .setTankAction(FluidTankComponent.Action.FILL)
                        .setValidator(this.type.getTankInputPredicates().get(i));
                this.addTank(slot);
                this.inputs[i] = slot;
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new GeneratorBackgroundScreenAddon(128, 39, type));
    }

    @Override
    public int consumeFuel() {
        Pair<Integer, Integer> inputs = this.type.getTimeAndPowerGeneration(this.inputs);
        this.powerGeneration = inputs.getRight();
        return inputs.getKey();
    }

    @Override
    public boolean canStart() {
        return this.type.canStart(this.inputs);
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return powerGeneration;
    }

    @Override
    public ProgressBarComponent<MycelialGeneratorTile> getProgressBar() {
        bar = new ProgressBarComponent<MycelialGeneratorTile>(30, 20, 0, 100) {
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
                        tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.eta").getString() + ChatFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * bar.getTickingTime() / 20D / bar.getProgressIncrease())) + ChatFormatting.DARK_AQUA + Component.translatable("tooltip.industrialforegoing.sec_short").getString()));
                        tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.generating").getString() + ChatFormatting.WHITE + powerGeneration + ChatFormatting.DARK_AQUA + " FE" + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + ChatFormatting.DARK_AQUA + "t"));
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
    public void serverTick(Level level, BlockPos pos, BlockState state, MycelialGeneratorTile blockEntity) {
        if (bar.getCanIncrease().test(this) && (bar.getProgress() != 0 || canStart()) && this.level.getGameTime() % 5 == 0) {
            MycelialDataManager.setGeneratorInfo(owner, this.level, this.worldPosition, this.type);
            type.onTick(this.level, this.worldPosition);
        }
        super.serverTick(level, pos, state, blockEntity);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        for (int i = 0; i < this.inputs.length; i++) {
            compoundTag.put("input_" + i, this.inputs[i].serializeNBT(provider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        for (int i = 0; i < this.inputs.length; i++) {
            this.inputs[i].deserializeNBT(provider, compound.getCompound("input_" + i));
        }
        super.loadAdditional(compound, provider);
    }

    @Override
    public int getEnergyCapacity() {
        return 100000;
    }

    @Override
    public int getExtractingEnergy() {
        return 100000;
    }

    @Nonnull
    @Override
    public MycelialGeneratorTile getSelf() {
        return this;
    }

    @Override
    public boolean isSmart() {
        return true;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
