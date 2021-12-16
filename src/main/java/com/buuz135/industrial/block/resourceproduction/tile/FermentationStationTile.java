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
import com.buuz135.industrial.config.machine.resourceproduction.FermentationStationConfig;
import com.buuz135.industrial.fluid.OreTitaniumFluidAttributes;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class FermentationStationTile extends IndustrialProcessingTile<FermentationStationTile> {

    @Save
    private SidedFluidTankComponent<FermentationStationTile> input;
    @Save
    private SidedFluidTankComponent<FermentationStationTile> output;
    @Save
    private SidedFluidTankComponent<FermentationStationTile> catalyst;
    @Save
    private int production;
    @Save
    private int seal;
    @Save
    private boolean isSealed;

    public FermentationStationTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<FermentationStationTile>) ModuleResourceProduction.FERMENTATION_STATION.get(), 90, 40, blockPos, blockState);
        addTank(this.input = (SidedFluidTankComponent<FermentationStationTile>) new SidedFluidTankComponent<FermentationStationTile>("input", 4000, 50, 20, 0) {
                    @Override
                    protected void onContentsChanged() {
                        syncObject(FermentationStationTile.this.input);
                    }

                    @Override
                    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
                        return Collections.emptyList();
                    }
                }
                        .setColor(DyeColor.BROWN)
                        .setTankAction(FluidTankComponent.Action.FILL)
                        .setValidator(fluidStack -> !isSealed && fluidStack.getFluid().isSame(ModuleCore.RAW_ORE_MEAT.getSourceFluid()))
        );
        addTank(this.output = (SidedFluidTankComponent<FermentationStationTile>) new SidedFluidTankComponent<FermentationStationTile>("output", 32000, 130, 20, 1) {
                    @Override
                    protected void onContentsChanged() {
                        syncObject(FermentationStationTile.this.output);
                    }

                    @Override
                    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
                        return Collections.emptyList();
                    }
                }
                        .setColor(DyeColor.ORANGE)
                        .setTankAction(FluidTankComponent.Action.DRAIN)
        );
        addTank(this.catalyst = (SidedFluidTankComponent<FermentationStationTile>) new SidedFluidTankComponent<FermentationStationTile>("catalyst", 2000, 90, 60, 2)
                .setColor(DyeColor.LIME)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.PINK_SLIME.getSourceFluid()) || fluidStack.getFluid().isSame(ModuleCore.ETHER.getSourceFluid()))
        );
        this.production = 0;
        this.seal = 0;
        this.isSealed = false;
        addButton(new ButtonComponent(75, 22, 14, 14) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() ->
                        new StateButtonAddon(this, Arrays.stream(SealType.values()).map(sealType -> sealType.info).toArray(StateButtonInfo[]::new)) {
                            @Override
                            public int getState() {
                                return seal;
                            }
                        });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.seal = (++this.seal)%SealType.values().length;
            syncObject(this.seal);
        }));
        addButton(new ButtonComponent(110, 22, 14, 14) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() ->
                        new StateButtonAddon(this, Arrays.stream(ProductionType.values()).map(sealType -> sealType.info).toArray(StateButtonInfo[]::new)) {
                            @Override
                            public int getState() {
                                return production;
                            }

                            @Override
                            public List<Component> getTooltipLines() {
                                ProductionType type = ProductionType.values()[production];
                                List<Component> list = new ArrayList<>(super.getTooltipLines());
                                list.add(new TranslatableComponent("text.industrialforegoing.tooltip.fermentation_station.time").append(type.getTicks() / 20 + "s"));
                                list.add(new TranslatableComponent("text.industrialforegoing.tooltip.fermentation_station.catalyst").append(type.getNeededFluid().isEmpty() ? "None" : new TranslatableComponent(type.getNeededFluid().getTranslationKey()).getString()));

                                return list;
                            }
                        });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.production= (++this.production)%ProductionType.values().length;
            this.getProgressBar().setMaxProgress(ProductionType.values()[this.production].getTicks());
            syncObject(this.production);
        }));
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, FermentationStationTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (seal < SealType.values().length) {
            boolean sealValue = SealType.values()[seal].canSeal.test(input);
            if (sealValue != this.isSealed) {
                markForUpdate();
            }
            this.isSealed = sealValue;
        }
    }

    @Override
    public boolean canIncrease() {
        ProductionType productionType = ProductionType.values()[this.production];
        int multipliedAmount = productionType.amount * this.input.getFluidAmount();
        return isSealed && this.output.getFluidAmount() + multipliedAmount <= this.output.getCapacity() && (productionType.neededFluid.isEmpty() || (!this.catalyst.isEmpty() && productionType.neededFluid.getFluid().isSame(this.catalyst.getFluid().getFluid()) && this.catalyst.getFluidAmount() >= (productionType.neededFluid.getAmount()  * this.input.getFluidAmount() / 100)));
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ProductionType productionType = ProductionType.values()[this.production];
            int multipliedAmount = productionType.amount * this.input.getFluidAmount();
            FluidStack stack = OreTitaniumFluidAttributes.getFluidWithTag(ModuleCore.FERMENTED_ORE_MEAT, multipliedAmount, new ResourceLocation(OreTitaniumFluidAttributes.getFluidTag(this.input.getFluid())));
            this.output.fillForced(stack, IFluidHandler.FluidAction.EXECUTE);
            this.catalyst.drainForced(productionType.neededFluid.getAmount() * this.input.getFluidAmount() / 100, IFluidHandler.FluidAction.EXECUTE);
            this.input.drainForced(this.input.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
        };
    }



    @Override
    protected int getTickPower() {
        return FermentationStationConfig.powerPerTick;
    }

    @Override
    public int getMaxProgress() {
        return ProductionType.values()[this.production].ticks;
    }

    @Override
    protected EnergyStorageComponent<FermentationStationTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(FermentationStationConfig.maxStoredPower, 10, 20);
    }

    @Nonnull
    @Override
    public FermentationStationTile getSelf() {
        return this;
    }

    public enum ProductionType {

        X_2(2,FermentationStationConfig.ticksFor2XProduction, FluidStack.EMPTY, new StateButtonInfo(0, IndustrialAssetProvider.FERMENTATION_PROCESSING_TWO, "text.industrialforegoing.tooltip.fermentation_station.processing_two")), //10 Seg
        X_3(3,FermentationStationConfig.ticksFor3XProduction, FluidStack.EMPTY, new StateButtonInfo(1, IndustrialAssetProvider.FERMENTATION_PROCESSING_THREE, "text.industrialforegoing.tooltip.fermentation_station.processing_three")), //45 seg
        X_4(4,FermentationStationConfig.ticksFor4XProduction, new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2), new StateButtonInfo(2, IndustrialAssetProvider.FERMENTATION_PROCESSING_FOUR, "text.industrialforegoing.tooltip.fermentation_station.processing_four")), //2 min
        X_5(5,FermentationStationConfig.ticksFor5XProduction, new FluidStack(ModuleCore.ETHER.getSourceFluid(), 1), new StateButtonInfo(3, IndustrialAssetProvider.FERMENTATION_PROCESSING_FIVE, "text.industrialforegoing.tooltip.fermentation_station.processing_five")) //5 min
        ;

        private final int amount;
        private final int ticks;
        private final FluidStack neededFluid;
        private final StateButtonInfo info;

        ProductionType(int amount, int ticks, FluidStack neededFluid, StateButtonInfo info) {
            this.amount = amount;
            this.ticks = ticks;
            this.neededFluid = neededFluid;
            this.info = info;
        }

        public int getTicks() {
            return ticks;
        }

        public FluidStack getNeededFluid() {
            return neededFluid;
        }
    }

    public enum SealType {

        FULL(sidedFluidTankComponent -> sidedFluidTankComponent.getFluidAmount() == sidedFluidTankComponent.getCapacity(), new StateButtonInfo(0, IndustrialAssetProvider.FERMENTATION_TANK_FULL, "text.industrialforegoing.tooltip.fermentation_station.tank_full")),
        HALF(sidedFluidTankComponent -> sidedFluidTankComponent.getFluidAmount() >= sidedFluidTankComponent.getCapacity() / 2, new StateButtonInfo(1, IndustrialAssetProvider.FERMENTATION_TANK_HALF, "text.industrialforegoing.tooltip.fermentation_station.tank_half")),
        ONE(sidedFluidTankComponent -> sidedFluidTankComponent.getFluidAmount() >= 1000, new StateButtonInfo(2, IndustrialAssetProvider.FERMENTATION_TANK_ONE, "text.industrialforegoing.tooltip.fermentation_station.tank_one"));

        private final Predicate<SidedFluidTankComponent> canSeal;
        private final StateButtonInfo info;

        SealType(Predicate<SidedFluidTankComponent> canSeal, StateButtonInfo info) {
            this.canSeal = canSeal;
            this.info = info;
        }
    }
}
