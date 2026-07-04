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

import com.buuz135.industrial.block.tile.IndustrialMachineTile;
import com.buuz135.industrial.config.machine.resourceproduction.FluidLaserBaseConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.buuz135.industrial.recipe.LaserDrillRarity;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FluidLaserBaseTile extends IndustrialMachineTile<FluidLaserBaseTile> implements ILaserBase<FluidLaserBaseTile> {

    @Save
    private ProgressBarComponent<FluidLaserBaseTile> work;
    @Save
    private SidedInventoryComponent<FluidLaserBaseTile> catalyst;
    @Save
    private SidedFluidTankComponent<FluidLaserBaseTile> output;
    @Save
    private int miningDepth;

    public FluidLaserBaseTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.FLUID_LASER_BASE, blockPos, blockState);
        setShowEnergy(false);
        this.miningDepth = this.getBlockPos().getY();
        this.addProgressBar(work = new ProgressBarComponent<FluidLaserBaseTile>(74, 24 + 18, 0, FluidLaserBaseConfig.maxProgress) {
                    @Override
                    @OnlyIn(Dist.CLIENT)
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon<FluidLaserBaseTile>(work.getPosX(), work.getPosY(), this) {
                            @Override
                            public List<Component> getTooltipLines() {
                                List<Component> tooltip = new ArrayList<>();
                                tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.titanium.progressbar.progress").getString() + ChatFormatting.WHITE + new DecimalFormat().format(work.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(work.getMaxProgress())));
                                return tooltip;
                            }
                        });
                    }
                }
                        .setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT)
                        .setCanIncrease(oreLaserBaseTile -> true)
                        .setProgressIncrease(0)
                        .setCanReset(oreLaserBaseTile -> true)
                        .setOnStart(() -> {
                            int maxProgress = (int) Math.floor(FluidLaserBaseConfig.maxProgress * (this.hasAugmentInstalled(AugmentTypes.EFFICIENCY) ? AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.EFFICIENCY).get(0), AugmentTypes.EFFICIENCY) : 1));
                            work.setMaxProgress(maxProgress);
                        })
                        .setOnFinishWork(this::onWork)
        );
        this.addInventory(catalyst = (SidedInventoryComponent<FluidLaserBaseTile>) new SidedInventoryComponent<FluidLaserBaseTile>("lens", 50, 24 + 18, 1, 0)
                        .setColor(DyeColor.BLUE)
                        .setRange(2, 3)
                        .setSlotLimit(1)
                //.setInputFilter((stack, integer) -> stack.getItem() instanceof LaserLensItem)
        );
        catalyst.getFacingModes().keySet().forEach(sideness -> catalyst.getFacingModes().put(sideness, IFacingComponent.FaceMode.NONE));
        this.addTank(output = (SidedFluidTankComponent<FluidLaserBaseTile>) new SidedFluidTankComponent<FluidLaserBaseTile>("output", 32000, 102, 20, 1)
                .setColor(DyeColor.ORANGE)
                .setTankAction(FluidTankComponent.Action.DRAIN)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(() -> new TextScreenAddon("", 70, 84 + 3, false) {
            @Override
            public String getText() {
                return ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.depth").getString();
            }
        });
        this.addGuiAddonFactory(() -> {
            var edit = new EditBox(Minecraft.getInstance().font, 80, 26, 40, 12, Component.literal(this.miningDepth + "")) {
                @Override
                public String getValue() {
                    return miningDepth + "";
                }
            };
            edit.setValue(miningDepth + "");
            edit.setFilter(string -> {
                if (string.isEmpty()) return true;
                try {
                    Integer.decode(string);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            edit.setResponder(string -> {
                if (!string.isEmpty()) {
                    CompoundTag compoundTag = new CompoundTag();
                    compoundTag.putInt("MiningLevel", Integer.decode(string));
                    Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(new TileEntityLocatorInstance(this.getBlockPos()), 5487, compoundTag));
                }
            });
            return new WidgetScreenAddon(102, 85, edit);
        });
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        if (id == 5487) {
            this.miningDepth = compound.getInt("MiningLevel");
            syncObject(this.miningDepth);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.miningDepth == -100) this.miningDepth = this.worldPosition.getY();
    }

    private void onWork() {
        ItemStack catalystStack = catalyst.getStackInSlot(0);
        if (catalystStack.isEmpty()) {
            return;
        }

        VoxelShape box = Shapes.box(-1, 0, -1, 2, 3, 2).move(this.worldPosition.getX(), this.worldPosition.getY() - 1, this.worldPosition.getZ());

        Holder<Biome> biome = this.level.getBiome(this.worldPosition);

        for (LaserDrillFluidRecipe recipe : RecipeUtil.getRecipes(this.level, (RecipeType<LaserDrillFluidRecipe>) ModuleCore.LASER_DRILL_FLUID_TYPE.get())) {
            if (!recipe.catalyst.test(catalystStack)) {
                continue;
            }

            if (LaserDrillRarity.getValidRarity(this.level, recipe.rarity, this.level.dimensionType(), biome, this.miningDepth) == null) {
                continue;
            }

            if (recipe.entityData.isPresent()) {
                var entityData = recipe.entityData.get();

                List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, box.bounds(), entity -> entity.getHealth() > 5 && entityData.getEntity().test(entity));

                for (var candidate : entities) {
                    if (!entityData.getData().isEmpty()) {
                        CompoundTag data = new CompoundTag();
                        candidate.saveWithoutId(data);
                        if (!NbtUtils.compareNbt(recipe.entityData.get().getData(), data, true)) {
                            continue;
                        }
                    }

                    candidate.hurt(candidate.damageSources().generic(), 5);
                    output.fillForced(recipe.output.getFluids()[0].copy(), IFluidHandler.FluidAction.EXECUTE);
                    return;
                }
            } else {
                output.fillForced(recipe.output.getFluids()[0].copy(), IFluidHandler.FluidAction.EXECUTE);
                return;
            }
        }
    }

    @Override
    public FluidLaserBaseTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<FluidLaserBaseTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(0, 4, 10);
    }

    @Override
    public ProgressBarComponent<FluidLaserBaseTile> getBar() {
        return work;
    }

    @Override
    public boolean canAcceptAugment(ItemStack augment) {
        if (AugmentWrapper.hasType(augment, AugmentTypes.EFFICIENCY)) {
            return true;
        }
        return false;
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        tag.putInt("FL_miningDepth", miningDepth);
        super.saveSettings(player, tag);
    }

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        if (tag.contains("FL_miningDepth")) {
            this.miningDepth = tag.getInt("FL_miningDepth");
        }
        super.loadSettings(player, tag);
    }
}
