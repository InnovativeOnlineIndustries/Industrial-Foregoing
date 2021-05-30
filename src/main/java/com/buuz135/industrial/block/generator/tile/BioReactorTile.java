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

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.config.machine.generator.BioReactorConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.bundle.LockableInventoryBundle;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BioReactorTile extends IndustrialWorkingTile<BioReactorTile> {

    public static ITag<Item>[] VALID = new ITag[]{IndustrialTags.Items.BIOREACTOR_INPUT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_NETHER_WART, Tags.Items.DYES,
            Tags.Items.HEADS, Tags.Items.MUSHROOMS, Tags.Items.SEEDS, ItemTags.SAPLINGS};

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<BioReactorTile> biofuel;
    @Save
    private SidedFluidTankComponent<BioReactorTile> water;
    @Save
    private LockableInventoryBundle<BioReactorTile> input;
    @Save
    private ProgressBarComponent<BioReactorTile> bar;

    public BioReactorTile() {
        super(ModuleGenerator.BIOREACTOR, BioReactorConfig.powerPerOperation);
        addTank(water = (SidedFluidTankComponent<BioReactorTile>) new SidedFluidTankComponent<BioReactorTile>("water", BioReactorConfig.maxWaterTankStorage, 45, 20, 0).
                setColor(DyeColor.CYAN).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().equals(Fluids.WATER))
        );
        addBundle(input = new LockableInventoryBundle<>(this, new SidedInventoryComponent<BioReactorTile>("input", 69, 22, 9, 1).
                setColor(DyeColor.BLUE).
                setRange(3, 3).
                setInputFilter((stack, integer) -> canInsert(integer - 2, stack)).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        , 136, 84, false));
        addTank(biofuel = (SidedFluidTankComponent<BioReactorTile>) new SidedFluidTankComponent<BioReactorTile>("biofuel", BioReactorConfig.maxBioFuelTankStorage, 74 + 18 * 3, 20, 2).
                setColor(DyeColor.PURPLE).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.BIOFUEL.getSourceFluid()))
        );
        addProgressBar(bar = new ProgressBarComponent<BioReactorTile>(96 + 18 * 3, 20, BioReactorConfig.maxProgress) {
                    @Override
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon<BioReactorTile>(bar.getPosX(), bar.getPosY(), this) {
                            @Override
                            public List<ITextComponent> getTooltipLines() {
                                return Arrays.asList(new StringTextComponent(TextFormatting.GOLD + "Efficiency: " + TextFormatting.WHITE + (int) ((getEfficiency() / 9D) * 100) + TextFormatting.DARK_AQUA + "%"));
                            }
                        });
                    }
                }.
                        setColor(DyeColor.YELLOW).
                        setCanIncrease(tileEntity -> true).
                        setOnTickWork(() -> bar.setProgress((int) ((getEfficiency() / 9D) * 100))).
                        setCanReset(tileEntity -> false).
                        setComponentHarness(this)
        );
        this.getMaxProgress = BioReactorConfig.maxProgress;
        this.getPowerPerOperation = BioReactorConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            int efficiency = getEfficiency();
            if (efficiency <= 0) return new WorkAction(1, 0);
            int fluidAmount = ((efficiency - 1) * 10 + 80) * efficiency;
            if (water.getFluidAmount() >= fluidAmount && biofuel.getCapacity() - biofuel.getFluidAmount() >= fluidAmount) {
                water.drainForced(fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                biofuel.fillForced(new FluidStack(ModuleCore.BIOFUEL.getSourceFluid(), fluidAmount), IFluidHandler.FluidAction.EXECUTE);
                for (int i = 0; i < input.getInventory().getSlots(); i++) {
                    input.getInventory().getStackInSlot(i).shrink(1);
                }
                return new WorkAction(1, getPowerPerOperation);
            }
        }
        return new WorkAction(1, 0);
    }

    private boolean canInsert(int slot, ItemStack stack) {
        int foundSlot = -1;
        for (int i = 0; i < input.getInventory().getSlots(); i++) {
            if (input.getInventory().getStackInSlot(i).isItemEqual(stack)) {
                foundSlot = i;
            }
        }
        for (ITag<Item> itemTag : VALID) {
            if (itemTag.contains(stack.getItem()) && (foundSlot == -1 || (input.getInventory().getStackInSlot(foundSlot).getCount() + stack.getCount() <= input.getInventory().getStackInSlot(foundSlot).getMaxStackSize() && slot == foundSlot)))
                return true; //contains
        }
        return false;
    }

    private int getEfficiency() {
        int slots = 0;
        for (int i = 0; i < input.getInventory().getSlots(); i++) {
            if (!input.getInventory().getStackInSlot(i).isEmpty()) {
                ++slots;
            }
        }
        return slots;
    }

    @Override
    protected EnergyStorageComponent<BioReactorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(BioReactorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public BioReactorTile getSelf() {
        return this;
    }
}
