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
import com.buuz135.industrial.config.machine.resourceproduction.OreLaserBaseConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.recipe.LaserDrillOreRecipe;
import com.buuz135.industrial.recipe.LaserDrillRarity;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium._impl.TagConfig;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OreLaserBaseTile extends IndustrialMachineTile<OreLaserBaseTile> implements ILaserBase<OreLaserBaseTile> {

    @Save
    private ProgressBarComponent<OreLaserBaseTile> work;
    @Save
    private SidedInventoryComponent<OreLaserBaseTile> lens;
    @Save
    private SidedInventoryComponent<OreLaserBaseTile> output;
    @Save
    private int miningDepth;

    public OreLaserBaseTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.ORE_LASER_BASE, blockPos, blockState);
        setShowEnergy(false);
        this.miningDepth = this.getBlockPos().getY();
        this.addProgressBar(work = new ProgressBarComponent<OreLaserBaseTile>(12, 22, 0, OreLaserBaseConfig.maxProgress) {
                    @Override
                    @OnlyIn(Dist.CLIENT)
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon<OreLaserBaseTile>(work.getPosX(), work.getPosY(), this) {
                            @Override
                            public List<Component> getTooltipLines() {
                                List<Component> tooltip = new ArrayList<>();
                                tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.titanium.progressbar.progress").getString() + ChatFormatting.WHITE + new DecimalFormat().format(work.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(work.getMaxProgress())));
                                return tooltip;
                            }
                        });
                    }
                }
                        .setColor(DyeColor.YELLOW)
                        .setCanIncrease(oreLaserBaseTile -> true)
                        .setProgressIncrease(0)
                        .setCanReset(oreLaserBaseTile -> true)
                        .setOnStart(() -> {
                            int maxProgress = (int) Math.floor(OreLaserBaseConfig.maxProgress * (this.hasAugmentInstalled(AugmentTypes.EFFICIENCY) ? AugmentWrapper.getType(this.getInstalledAugments(AugmentTypes.EFFICIENCY).get(0), AugmentTypes.EFFICIENCY) : 1));
                            work.setMaxProgress(maxProgress);
                        })
                        .setOnFinishWork(this::onWork)
        );
        this.addInventory(lens = (SidedInventoryComponent<OreLaserBaseTile>) new SidedInventoryComponent<OreLaserBaseTile>("lens", 30, 24, 6, 0)
                        .setColor(DyeColor.BLUE)
                        .setRange(2, 3)
                        .setSlotLimit(1)
                //.setInputFilter((stack, integer) -> stack.getItem() instanceof LaserLensItem)
        );
        lens.getFacingModes().keySet().forEach(sideness -> lens.getFacingModes().put(sideness, IFacingComponent.FaceMode.NONE));
        this.addInventory(output = (SidedInventoryComponent<OreLaserBaseTile>) new SidedInventoryComponent<OreLaserBaseTile>("output", 20 + 18 * 3, 24, 5 * 3, 1)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setInputFilter((stack, integer) -> false)
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
                if (string.isEmpty() || string.equals("-")) return true;
                try {
                    Integer.decode(string);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            edit.setResponder(string -> {
                if (!string.isEmpty() && !string.equals("-")) {
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
        if (!ItemStackUtils.isInventoryFull(this.output)) {
            List<ItemStackWeightedItem> items = RecipeUtil.getRecipes(this.level, (RecipeType<LaserDrillOreRecipe>) ModuleCore.LASER_DRILL_TYPE.get()).stream()
                    .filter(laserDrillOreRecipe -> LaserDrillRarity.getValidRarity(this.level, laserDrillOreRecipe.rarity, this.level.dimensionType(), this.level.getBiome(this.worldPosition), this.miningDepth) != null)
                    .map(laserDrillOreRecipe -> {
                        var rarity = LaserDrillRarity.getValidRarity(this.level, laserDrillOreRecipe.rarity, this.level.dimensionType(), this.level.getBiome(this.worldPosition), this.miningDepth);
                        int weight = rarity.weight();
                        for (int i = 0; i < lens.getSlots(); i++) {
                            if (laserDrillOreRecipe.catalyst.test(lens.getStackInSlot(i)))
                                weight += OreLaserBaseConfig.catalystModifier;
                        }
                        ItemStack stack = ItemStack.EMPTY;
                        for (String modid : TagConfig.ITEM_PREFERENCE) {
                            for (ItemStack matchingStack : laserDrillOreRecipe.output.getItems()) {
                                if (BuiltInRegistries.ITEM.getKey(matchingStack.getItem()).getNamespace().equals(modid)) {
                                    stack = matchingStack;
                                    break;
                                }
                            }
                            if (!stack.isEmpty()) break;
                        }
                        if (stack.isEmpty()) stack = laserDrillOreRecipe.output.getItems()[0];
                        return new ItemStackWeightedItem(stack.copy(), weight);
                    }).toList();
            if (!items.isEmpty()) {
                ItemStack stack = WeightedRandom.getRandomItem(this.level.getRandom(), items).get().getStack();
                ItemHandlerHelper.insertItem(output, stack, false);
            }
        }
    }

    @Override
    public OreLaserBaseTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<OreLaserBaseTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(0, 4, 10);
    }

    @Override
    public ProgressBarComponent<OreLaserBaseTile> getBar() {
        return work;
    }

    @Override
    public boolean canAcceptAugment(ItemStack augment) {
        if (AugmentWrapper.hasType(augment, AugmentTypes.SPEED)) {
            return false;
        }
        return super.canAcceptAugment(augment);
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        tag.putInt("OL_miningDepth", miningDepth);
        super.saveSettings(player, tag);
    }

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        if (tag.contains("OL_miningDepth")) {
            this.miningDepth = tag.getInt("OL_miningDepth");
        }
        super.loadSettings(player, tag);
    }

}
