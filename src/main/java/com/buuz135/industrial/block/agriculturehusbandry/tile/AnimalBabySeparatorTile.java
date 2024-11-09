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

package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.AnimalBabySeparatorConfig;
import com.buuz135.industrial.gui.component.ItemGuiAddon;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class AnimalBabySeparatorTile extends IndustrialAreaWorkingTile<AnimalBabySeparatorTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private boolean movingAdults;

    public AnimalBabySeparatorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.ANIMAL_BABY_SEPARATOR, RangeManager.RangeType.BEHIND, true, AnimalBabySeparatorConfig.powerPerOperation, blockPos, blockState);
        this.movingAdults = false;
        addButton(new ButtonComponent(42, 20, 18, 18) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new StateButtonAddon(this, new StateButtonInfo(0, AssetTypes.ITEM_BACKGROUND, Component.translatable("tooltip.industrialforegoing.animal_baby_separator.moving_baby").getString()), new StateButtonInfo(1, AssetTypes.ITEM_BACKGROUND, Component.translatable("tooltip.industrialforegoing.animal_baby_separator.moving_adult").getString())) {
                    @Override
                    public int getState() {
                        return movingAdults ? 1 : 0;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            movingAdults = !movingAdults;
            markForUpdate();
        }));
        this.maxProgress = AnimalBabySeparatorConfig.maxProgress;
        this.powerPerOperation = AnimalBabySeparatorConfig.powerPerOperation;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new ItemGuiAddon(42, 20) {
            @Override
            public ItemStack getItemStack() {
                return new ItemStack(movingAdults ? Items.WHEAT : Items.WHEAT_SEEDS);
            }
        }.withoutTooltip());
    }

    @Override
    public WorkAction work() {
        if (this.level != null && hasEnergy(powerPerOperation)) {
            List<Animal> mobs = this.level.getEntitiesOfClass(Animal.class, getWorkingArea().bounds());
            mobs.removeIf(animalEntity -> !animalEntity.isBaby() == !movingAdults);
            if (mobs.size() == 0) return new WorkAction(1, 0);
            BlockPos pos = this.getBlockPos().relative(this.getFacingDirection());
            mobs.get(0).setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            return new WorkAction(0.25f, powerPerOperation);
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<AnimalBabySeparatorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(AnimalBabySeparatorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public AnimalBabySeparatorTile getSelf() {
        return this;
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        tag.putBoolean("ABS_movingAdults", movingAdults);
        super.saveSettings(player, tag);
    }

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        if (tag.contains("ABS_movingAdults")) this.movingAdults = tag.getBoolean("ABS_movingAdults");
        super.loadSettings(player, tag);
    }
}
