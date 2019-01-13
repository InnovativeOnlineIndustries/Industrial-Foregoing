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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.ndrei.teslacorelib.gui.*;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CropSowerTile extends WorkingAreaElectricMachine {

    private static final String NBT_POINTER = "pointer";

    public static EnumDyeColor[] colors = new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.CYAN, EnumDyeColor.PURPLE, EnumDyeColor.YELLOW, EnumDyeColor.WHITE, EnumDyeColor.MAGENTA, EnumDyeColor.LIME, EnumDyeColor.BLUE, EnumDyeColor.BLACK};

    private LockableItemHandler inPlant;
    private int pointer;
    private boolean hoeGround;

    public CropSowerTile() {
        super(CropSowerTile.class.getName().hashCode());
        this.hoeGround = true;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inPlant = new LockableItemHandler(3 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                CropSowerTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 16;
            }
        };
        this.addInventory(new ColoredItemHandler(inPlant, EnumDyeColor.GREEN, "Seeds input", new BoundingRectangle(18 * 3, 25, 18 * 3, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return super.canInsertItem(slot, stack) && (stack.getItem() instanceof IPlantable || ItemStackUtils.isStackOreDict(stack, "treeSapling") || ItemStackUtils.isChorusFlower(stack));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
                List<IGuiContainerPiece> guiContainerPieces = super.getGuiContainerPieces(container);
                int i = 0;
                for (int x = 0; x < 3; ++x) {
                    for (int y = 0; y < 3; ++y) {
                        guiContainerPieces.add(new TiledRenderedGuiPiece(18 * 3 + 18 * x, 25 + 18 * y, 18, 18, 1, 1, BasicTeslaGuiContainer.Companion.getMACHINE_BACKGROUND(), 108, 225, colors[i]));
                        ++i;
                    }
                }
                return guiContainerPieces;
            }
        });
        this.addInventoryToStorage(inPlant, "inPlant");
        this.registerSyncIntPart("hoe", nbtTagInt -> hoeGround = nbtTagInt.getInt() == 1, () -> new NBTTagInt(hoeGround ? 1 : 0), SyncProviderLevel.GUI);
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return super.getWorkingArea().offset(0, 1, 0);
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        ++pointer;
        if (pointer >= blockPos.size()) pointer = 0;
        if (pointer < blockPos.size()) {
            BlockPos pos = blockPos.get(pointer);
            if (this.world.isAirBlock(pos) && !this.world.isAirBlock(pos.down())) {
                FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                ItemStack stack = inPlant.getStackInSlot(getFilteredSlot(pos));
                if (stack.isEmpty() && inPlant.getLocked()) {
                    ItemStack filter = inPlant.getFilterStack(getFilteredSlot(pos));
                    for (int i = 0; i < inPlant.getSlots(); ++i) {
                        if (!inPlant.getStackInSlot(i).isEmpty() && inPlant.getStackInSlot(i).isItemEqual(filter)) {
                            stack = inPlant.getStackInSlot(i);
                            break;
                        }
                    }
                }
                if (!stack.isEmpty()) {
                    if (hoeGround && !ItemStackUtils.isChorusFlower(stack) && !ItemStackUtils.isStackOreDict(stack, "treeSapling") && (this.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().equals(Blocks.DIRT) || this.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().equals(Blocks.GRASS))) {
                        this.world.setBlockState(pos.offset(EnumFacing.DOWN), Blocks.FARMLAND.getDefaultState());
                    }
                    player.setHeldItem(EnumHand.MAIN_HAND, stack);
                    if (stack.getItem().getRegistryName() != null && stack.getItem().getRegistryName().getNamespace().equals("forestry")) {
                        player.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 90, 90);
                        stack.useItemRightClick(this.world, player, EnumHand.MAIN_HAND);
                        return 1;
                    }
                    EnumActionResult result = stack.onItemUse(player, this.world, pos.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
                    return result == EnumActionResult.SUCCESS ? 1 : 0;
                }
            }
        } else {
            pointer = 0;
        }
        return 0.1f;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_POINTER, pointer);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_POINTER)) pointer = 0;
        else pointer = compound.getInteger(NBT_POINTER);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(1, new LockedInventoryTogglePiece(18 * 6 + 2, 27, this, EnumDyeColor.GREEN));
        pieces.add(new ToggleButtonPiece(118, 84, 13, 13, 0) {

            @Override
            protected int getCurrentState() {
                return hoeGround ? 1 : 0;
            }

            @Override
            protected void renderState(BasicTeslaGuiContainer container, int state, BoundingRectangle box) {

            }

            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
                container.drawTexturedRect(this.getLeft() - 1, this.getTop() - 1, 49, 56, 16, 16);
                ItemStackUtils.renderItemIntoGUI(hoeGround ? new ItemStack(Items.MELON_SEEDS, 1, 0) : new ItemStack(Blocks.RED_FLOWER, 1, 3), this.getLeft() + guiX - 2, this.getTop() + guiY - (hoeGround ? 1 : 5), 9);
            }

            @Override
            protected void clicked() {
                CropSowerTile.this.sendToServer(CropSowerTile.this.setupSpecialNBTMessage("HOE"));
            }

            @NotNull
            @Override
            protected List<String> getStateToolTip(int state) {
                return Arrays.asList(new TextComponentTranslation("text.industrialforegoing.button.sower.hoe." + hoeGround).getFormattedText());
            }
        });
        return pieces;
    }

    @Nullable
    @Override
    protected SimpleNBTMessage processClientMessage(@Nullable String messageType, @NotNull NBTTagCompound compound) {
        if (messageType != null && messageType.equalsIgnoreCase("HOE")) {
            hoeGround = !hoeGround;
            markDirty();
            forceSync();
        }
        return super.processClientMessage(messageType, compound);
    }

    private int getFilteredSlot(BlockPos pos) {
        int radius = getRadius();
        if (radius == 0) {
            for (int i = 0; i < inPlant.getSlots(); ++i) {
                if (!inPlant.getStackInSlot(i).isEmpty()) {
                    return i;
                }
            }
        }
        int x = Math.round(1.49F * (pos.getX() - this.pos.getX()) / radius);
        int z = Math.round(1.49F * (pos.getZ() - this.pos.getZ()) / radius);
        return 4 + x + 3 * z;
    }
}
