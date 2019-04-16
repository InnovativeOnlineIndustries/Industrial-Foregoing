/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.item.LaserLensItem;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.client.infopiece.ArrowInfoPiece;
import com.buuz135.industrial.proxy.client.infopiece.IHasDisplayString;
import com.buuz135.industrial.proxy.client.infopiece.LaserBaseInfoPiece;
import com.buuz135.industrial.proxy.client.infopiece.TextInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LaserBaseTile extends CustomSidedTileEntity implements IHasDisplayString {

    private static String NBT_CURRENT = "currentWork";

    private int depth;

    private int currentWork;
    private ItemStackHandler outItems;
    private ItemStackHandler lensItems;

    public LaserBaseTile() {
        super(LaserBaseTile.class.getName().hashCode());
        currentWork = 0;
        depth = 0;
        registerSyncIntPart("Depth", nbtTagInt -> depth = nbtTagInt.getInt(), () -> new NBTTagInt(depth), SyncProviderLevel.GUI);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        lensItems = new ItemStackHandler(2 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                LaserBaseTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(lensItems, EnumDyeColor.GREEN, "Lens items", 18 * 2, 25, 2, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem() instanceof LaserLensItem;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        this.addInventoryToStorage(lensItems, "lensItems");

        outItems = new ItemStackHandler(3 * 6) {
            @Override
            protected void onContentsChanged(int slot) {
                LaserBaseTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output items", 18 * 4 + 6, 25, 6, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outItems, "outItems");
        registerSyncIntPart(NBT_CURRENT, nbtTagInt -> currentWork = nbtTagInt.getInt(), () -> new NBTTagInt(currentWork), SyncProviderLevel.GUI);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new LaserBaseInfoPiece(this, 10, 25));

        pieces.add(new ArrowInfoPiece(153, 85, 1, 104, "text.industrialforegoing.button.increase_depth") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    if (GuiScreen.isShiftKeyDown()) {
                        LaserBaseTile.this.sendToServer(LaserBaseTile.this.setupSpecialNBTMessage("DEPTH_INCREASE_10"));
                    } else {
                        LaserBaseTile.this.sendToServer(LaserBaseTile.this.setupSpecialNBTMessage("DEPTH_INCREASE"));
                    }
                }
            }
        });
        pieces.add(new ArrowInfoPiece(117, 85, 16, 104, "text.industrialforegoing.button.decrease_depth") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    if (GuiScreen.isShiftKeyDown()) {
                        LaserBaseTile.this.sendToServer(LaserBaseTile.this.setupSpecialNBTMessage("DEPTH_DECREASE_10"));
                    } else {
                        LaserBaseTile.this.sendToServer(LaserBaseTile.this.setupSpecialNBTMessage("DEPTH_DECREASE"));
                    }
                }
            }
        });
        pieces.add(new TextInfoPiece(this, 1, 132, 87));
        return pieces;
    }

    @Nullable
    @Override
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        super.processClientMessage(messageType, compound);
        if (messageType.equals("DEPTH_INCREASE_10")) {
            depth = Math.min(depth + 10, 255);
            partialSync("Depth", true);
        }
        if (messageType.equals("DEPTH_INCREASE")) {
            depth = Math.min(depth + 1, 255);
            partialSync("Depth", true);
        }
        if (messageType.equals("DEPTH_DECREASE_10")) {
            depth = Math.max(depth - 10, 0);
            partialSync("Depth", true);
        }
        if (messageType.equals("DEPTH_DECREASE")) {
            depth = Math.max(depth - 1, 0);
            partialSync("Depth", true);
        }
        return null;
    }

    @Override
    protected void innerUpdate() {
        if (this.world.isRemote) return;
        if (currentWork >= getMaxWork()) {
            List<ItemStackWeightedItem> items = new ArrayList<>();
            LaserDrillEntry.LASER_DRILL_ENTRIES[this.depth].forEach(entry -> {
                if (
                        entry.getWhitelist().isEmpty() ||
                                entry.getWhitelist().contains(this.getWorld().getBiome(this.getPos()))
                ) {
                    if (!entry.getBlacklist().contains(this.getWorld().getBiome(this.getPos()))) {
                        int increase = 0;
                        for (int i = 0; i < lensItems.getSlots(); ++i) {
                            if (
                                    !lensItems.getStackInSlot(i).isEmpty()
                                            &&
                                            lensItems.getStackInSlot(i).getMetadata() == entry.getLaserMeta()
                                            &&
                                            lensItems.getStackInSlot(i).getItem() instanceof LaserLensItem
                            ) {
                                if (((LaserLensItem) lensItems.getStackInSlot(i).getItem()).isInverted()) {
                                    increase -= BlockRegistry.laserBaseBlock.getLenseChanceIncrease();
                                } else {
                                    increase += BlockRegistry.laserBaseBlock.getLenseChanceIncrease();
                                }
                            }
                        }
                        items.add(new ItemStackWeightedItem(entry.getStack(), entry.getWeight() + increase));
                    }
                }
            });

            if (!items.isEmpty()) {
                ItemStack stack = WeightedRandom.getRandomItem(this.world.rand, items).getStack().copy();
                if (ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
                    ItemHandlerHelper.insertItem(outItems, stack, false);
                }
            }
            currentWork = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        currentWork = 0;
        depth = 0;
        if (compound.hasKey(NBT_CURRENT)) currentWork = compound.getInteger(NBT_CURRENT);
        if (compound.hasKey("Depth")) depth = compound.getInteger("Depth");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(NBT_CURRENT, currentWork);
        compound.setInteger("Depth", depth);
        return super.writeToNBT(compound);
    }

    public int getCurrentWork() {
        return currentWork;
    }

    public int getDepth() {
        return depth;
    }

    public int getMaxWork() {
        return BlockRegistry.laserBaseBlock.getWorkNeeded();
    }

    public void increaseWork() {
        ++currentWork;
        partialSync(NBT_CURRENT, true);
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

    @Override
    protected boolean getShowRedstoneControlPiece() {
        return false;
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    public String getString(int id) {
        return "" + TextFormatting.DARK_GRAY + this.getDepth();
    }
}
