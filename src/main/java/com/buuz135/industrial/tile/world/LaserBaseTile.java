package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.client.infopiece.LaserBaseInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.block.LaserBaseBlock;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import java.util.ArrayList;
import java.util.List;

public class LaserBaseTile extends SidedTileEntity {

    private static String NBT_CURRENT = "currentWork";

    private int currentWork;
    private ItemStackHandler outItems;
    private ItemStackHandler lensItems;

    public LaserBaseTile() {
        super(LaserBaseTile.class.getName().hashCode());
        currentWork = 0;
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
                return stack.getItem().equals(ItemRegistry.laserLensItem);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        this.addInventoryToStorage(lensItems, "lensItems");

        outItems = new ItemStackHandler(3 * 5) {
            @Override
            protected void onContentsChanged(int slot) {
                LaserBaseTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output items", 18 * 4 + 6, 25, 5, 3) {
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
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new LaserBaseInfoPiece(this, 10, 25));
        return pieces;
    }

    @Override
    protected void innerUpdate() {
        if (this.world.isRemote) return;
        if (currentWork >= getMaxWork()) {
            List<ItemStackWeightedItem> items = new ArrayList<>();
            BlockRegistry.laserBaseBlock.getColoreOres().keySet().forEach(integer -> BlockRegistry.laserBaseBlock.getColoreOres().get(integer).forEach(itemStackWeightedItem -> {
                int increase = 0;
                for (int i = 0; i < lensItems.getSlots(); ++i) {
                    if (lensItems.getStackInSlot(i).getMetadata() == integer) {
                        increase += BlockRegistry.laserBaseBlock.getLenseChanceIncrease();
                    }
                }
                items.add(new ItemStackWeightedItem(itemStackWeightedItem.getStack(), itemStackWeightedItem.itemWeight + increase));
            }));
            ItemStack stack = WeightedRandom.getRandomItem(this.world.rand, items).getStack().copy();
            if (ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
                ItemHandlerHelper.insertItem(outItems, stack, false);
            }
            currentWork = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        currentWork = 0;
        if (compound.hasKey(NBT_CURRENT)) currentWork = compound.getInteger(NBT_CURRENT);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(NBT_CURRENT, currentWork);
        return super.writeToNBT(compound);
    }

    public int getCurrentWork() {
        return currentWork;
    }

    public int getMaxWork() {
        return ((LaserBaseBlock) this.getBlockType()).getWorkNeeded();
    }

    public void increaseWork() {
        ++currentWork;
    }

}
