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
package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.DyeMixerConfig;
import com.buuz135.industrial.gui.component.ItemGuiAddon;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DyeMixerTile extends IndustrialProcessingTile<DyeMixerTile> {

    private static ColorUsage[] colorUsages = {new ColorUsage(1, 1, 1), //0
            new ColorUsage(1, 1, 1),//1
            new ColorUsage(1, 0, 1),//2
            new ColorUsage(0, 0, 1),//3
            new ColorUsage(0, 1, 1),//4
            new ColorUsage(0, 1, 0),//5
            new ColorUsage(1, 0, 0),//6
            new ColorUsage(1, 1, 1),//7
            new ColorUsage(1, 1, 1),//8
            new ColorUsage(0, 0, 1),//9
            new ColorUsage(1, 0, 1),//10
            new ColorUsage(0, 0, 3),//11
            new ColorUsage(1, 1, 1),//12
            new ColorUsage(0, 3, 0),//13
            new ColorUsage(3, 0, 0),//14
            new ColorUsage(1, 1, 1)};//15

    private int getPowerPerTick;

    @Save
    private SidedInventoryComponent<DyeMixerTile> inputRed;
    @Save
    private SidedInventoryComponent<DyeMixerTile> inputGreen;
    @Save
    private SidedInventoryComponent<DyeMixerTile> inputBlue;
    @Save
    private ProgressBarComponent<DyeMixerTile> red;
    @Save
    private ProgressBarComponent<DyeMixerTile> green;
    @Save
    private ProgressBarComponent<DyeMixerTile> blue;
    @Save
    private SidedInventoryComponent<DyeMixerTile> output;
    @Save
    private int dye;

    public DyeMixerTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<DyeMixerTile>) ModuleResourceProduction.DYE_MIXER.get(), 96, 40, blockPos,blockState);
        addInventory(this.inputRed = (SidedInventoryComponent<DyeMixerTile>) new SidedInventoryComponent<DyeMixerTile>("input_red", 33, 21, 1, 0)
                .setColor(DyeColor.RED)
                .setInputFilter((stack, integer) -> stack.is(Tags.Items.DYES_RED))
                .setOutputFilter((stack, integer) -> false)
                .setComponentHarness(this)
        );
        addInventory(this.inputGreen = (SidedInventoryComponent<DyeMixerTile>) new SidedInventoryComponent<DyeMixerTile>("input_green", 33, 22 + 18, 1, 1)
                .setColor(DyeColor.GREEN)
                .setInputFilter((stack, integer) -> stack.is(Tags.Items.DYES_GREEN))
                .setOutputFilter((stack, integer) -> false)
                .setComponentHarness(this)
        );
        addInventory(this.inputBlue = (SidedInventoryComponent<DyeMixerTile>) new SidedInventoryComponent<DyeMixerTile>("input_blue", 33, 23 + 18 * 2, 1, 2)
                .setColor(DyeColor.BLUE)
                .setInputFilter((stack, integer) -> stack.is(Tags.Items.DYES_BLUE))
                .setOutputFilter((stack, integer) -> false)
                .setComponentHarness(this)
        );
        addProgressBar(this.red = new ProgressBarComponent<DyeMixerTile>(33 + 20, 20, 300) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon<DyeMixerTile>(red.getPosX(), red.getPosY(), this) {
                    @Override
                    public List<Component> getTooltipLines() {
                        return Arrays.asList(new TextComponent(ChatFormatting.GOLD + "Amount: " + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(red.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(red.getMaxProgress())));
                    }
                });
            }
        }
                .setCanIncrease(tileEntity -> false)
                .setCanReset(tileEntity -> false)
                .setColor(DyeColor.RED));
        addProgressBar(this.blue = new ProgressBarComponent<DyeMixerTile>(33 + 20 + 13, 20, 300) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon<DyeMixerTile>(blue.getPosX(), blue.getPosY(), this) {
                    @Override
                    public List<Component> getTooltipLines() {
                        return Arrays.asList(new TextComponent(ChatFormatting.GOLD + "Amount: " + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(blue.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(blue.getMaxProgress())));
                    }
                });
            }
        }
                .setCanIncrease(tileEntity -> false)
                .setCanReset(tileEntity -> false)
                .setColor(DyeColor.BLUE));
        addProgressBar(this.green = new ProgressBarComponent<DyeMixerTile>(33 + 20 + 13 * 2, 20, 300) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon<DyeMixerTile>(green.getPosX(), green.getPosY(), this) {
                    @Override
                    public List<Component> getTooltipLines() {
                        return Arrays.asList(new TextComponent(ChatFormatting.GOLD + "Amount: " + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(green.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(green.getMaxProgress())));
                    }
                });
            }
        }
                .setCanIncrease(tileEntity -> false)
                .setCanReset(tileEntity -> false)
                .setColor(DyeColor.GREEN));
        addInventory(this.output = (SidedInventoryComponent<DyeMixerTile>) new SidedInventoryComponent<DyeMixerTile>("output", 134, 20 + 38, 1, 3)
                .setColor(DyeColor.ORANGE)
                .setInputFilter((stack, integer) -> false)
                .setComponentHarness(this)
        );
        addButton(new ArrowButtonComponent(116, 22, 14, 14, FacingUtil.Sideness.LEFT).setId(1)
                .setPredicate((playerEntity, compoundNBT) -> {
                    --dye;
                    if (dye < 0) dye = 15;
                    markForUpdate();
                }));
        addButton(new ArrowButtonComponent(154, 22, 14, 14, FacingUtil.Sideness.RIGHT).setId(2)
                .setPredicate((playerEntity, compoundNBT) -> {
                    ++dye;
                    if (dye > 15) dye = 0;
                    markForUpdate();
                })
        );
        addGuiAddonFactory(() -> new ItemGuiAddon(133, 20) {
            @Override
            public ItemStack getItemStack() {
                return new ItemStack(DyeItem.byColor(DyeColor.byId(dye)));
            }
        });
        this.getPowerPerTick = DyeMixerConfig.powerPerTick;
    }

    @Override
    public boolean canIncrease() {
        increaseBar(inputRed.getStackInSlot(0), red);
        increaseBar(inputGreen.getStackInSlot(0), green);
        increaseBar(inputBlue.getStackInSlot(0), blue);
        ColorUsage color = colorUsages[dye];
        ItemStack dye = new ItemStack(DyeItem.byColor(DyeColor.byId(this.dye)));
        return red.getProgress() >= color.r && green.getProgress() >= color.g && blue.getProgress() >= color.b && ItemHandlerHelper.insertItem(output, dye, true).isEmpty();
    }

    private void increaseBar(ItemStack stack, ProgressBarComponent bar) {
        if (bar.getProgress() + 3 <= bar.getMaxProgress() && !stack.isEmpty()) {
            stack.shrink(1);
            bar.setProgress(bar.getProgress() + 3);
            markForUpdate();
        }
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack dye = new ItemStack(DyeItem.byColor(DyeColor.byId(this.dye)));
            if (ItemHandlerHelper.insertItem(output, dye, true).isEmpty()) {
                ColorUsage color = colorUsages[this.dye];
                red.setProgress(red.getProgress() - color.r);
                green.setProgress(green.getProgress() - color.g);
                blue.setProgress(blue.getProgress() - color.b);
                ItemHandlerHelper.insertItem(output, dye, false);
                markForUpdate();
            }
        };
    }

    @Override
    protected EnergyStorageComponent<DyeMixerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(DyeMixerConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return 30;
    }

    @Nonnull
    @Override
    public DyeMixerTile getSelf() {
        return this;
    }

    private static class ColorUsage {

        private int r;
        private int g;
        private int b;

        public ColorUsage(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }
    }
}
