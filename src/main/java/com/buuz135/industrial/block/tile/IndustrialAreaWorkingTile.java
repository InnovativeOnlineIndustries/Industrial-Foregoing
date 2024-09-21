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

import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.proxy.client.IndustrialAssetProvider;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.module.BlockWithTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class IndustrialAreaWorkingTile<T extends IndustrialAreaWorkingTile<T>> extends IndustrialWorkingTile<T> {

    @Save
    private int pointer;
    @Save
    private boolean showingArea;
    @Save
    private boolean spawnParticles;
    private ButtonComponent areaButton;
    private ButtonComponent particleButton;
    private RangeManager.RangeType type;
    private boolean acceptsRangeUpgrades;

    public IndustrialAreaWorkingTile(BlockWithTile basicTileBlock, RangeManager.RangeType type, boolean acceptsRangeUpgrades, int estimatedPower, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, estimatedPower, blockPos, blockState);
        this.pointer = 0;
        this.showingArea = false;
        this.spawnParticles = true;
        addButton(areaButton = new ButtonComponent(154 - 18, 84, 14, 14) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
                addons.add(() -> new StateButtonAddon(areaButton, new StateButtonInfo(0, IndustrialAssetProvider.BUTTON_SHOW_AREA, "text.industrialforegoing.button.show_area"), new StateButtonInfo(1, IndustrialAssetProvider.BUTTON_HIDE_AREA, "text.industrialforegoing.button.hide_area")) {
                    @Override
                    public int getState() {
                        return showingArea ? 1 : 0;
                    }
                });
                return addons;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.showingArea = !this.showingArea;
            this.markForUpdate();
        }));
        addButton(particleButton = new ButtonComponent(154 - 18 - 18, 84, 14, 14) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
                addons.add(() -> new StateButtonAddon(particleButton, new StateButtonInfo(0, IndustrialAssetProvider.BUTTON_HIDE_PARTICLE, "text.industrialforegoing.button.hide_particles"), new StateButtonInfo(1, IndustrialAssetProvider.BUTTON_SHOW_PARTICLE, "text.industrialforegoing.button.show_particles")) {
                    @Override
                    public int getState() {
                        return spawnParticles ? 0 : 1;
                    }
                });
                return addons;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.spawnParticles = !this.spawnParticles;
            this.markForUpdate();
        }));
        this.type = type;
        this.acceptsRangeUpgrades = acceptsRangeUpgrades;
    }

    public VoxelShape getWorkingArea() {
        return new RangeManager(this.worldPosition, this.getFacingDirection(), this.type).get(hasAugmentInstalled(RangeAddonItem.RANGE) ? ((int) AugmentWrapper.getType(getInstalledAugments(RangeAddonItem.RANGE).get(0), RangeAddonItem.RANGE) + 1) : 0);
    }

    public BlockPos getPointedBlockPos() {
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea().bounds());
        pointer = safetyPointerCheck(blockPosList);
        return blockPosList.get(pointer);
    }

    private int safetyPointerCheck(List<BlockPos> blockPosList) {
        return pointer < blockPosList.size() ? pointer : 0;
    }

    public void increasePointer() {
        BlockPos pointed = getPointedBlockPos();
        if (this.level instanceof ServerLevel && this.spawnParticles) {
            ((ServerLevel) this.level).sendParticles(new DustParticleOptions(new Vector3f(Math.abs(this.worldPosition.getX() % 255) / 256f, Math.abs(this.worldPosition.getY() % 255) / 256f, Math.abs(this.worldPosition.getZ() % 255) / 256f), 1f), pointed.getX() + 0.5, pointed.getY() + 1, pointed.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
        ++pointer;
    }

    public boolean isShowingArea() {
        return showingArea;
    }

    public boolean isLoaded(BlockPos pos) {
        return level.hasChunksAt(pos, pos);
    }

    @Override
    public boolean canAcceptAugment(ItemStack augment) {
        if (AugmentWrapper.hasType(augment, RangeAddonItem.RANGE))
            return super.canAcceptAugment(augment) && acceptsRangeUpgrades;
        return super.canAcceptAugment(augment);
    }

    public boolean isSpawnParticles() {
        return spawnParticles;
    }

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        super.loadSettings(player, tag);
        this.spawnParticles = tag.getBoolean("WA_spawnParticles");
        this.showingArea = tag.getBoolean("WA_showingArea");
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        super.saveSettings(player, tag);
        tag.putBoolean("WA_spawnParticles", this.spawnParticles);
        tag.putBoolean("WA_showingArea", this.showingArea);
    }
}
