package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.client.BlackHoleInfoPiece;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class BlackHoleUnitTile extends SidedTileEntity {

    public static final String NBT_ITEMSTACK = "itemstack";
    public static final String NBT_AMOUNT = "amount";
    public static final String NBT_META = "meta";
    public static final String NBT_ITEM_NBT = "stack_nbt";
    //TODO Save NBT item
    private ItemStackHandler inItems;
    private ItemStackHandler outItems;
    private ItemStack stack;
    private int amount;

    public BlackHoleUnitTile() {
        super(BlackHoleUnitTile.class.getName().hashCode());
        stack = ItemStack.EMPTY;
        amount = 0;
    }

    @Override
    protected void innerUpdate() {
        if (!inItems.getStackInSlot(0).isEmpty()) {
            ItemStack in = inItems.getStackInSlot(0);
            if (stack.isEmpty()) {
                stack = in;
                amount = 0;
            }
            amount += in.getCount();
            inItems.setStackInSlot(0, ItemStack.EMPTY);
        }
        if (outItems.getStackInSlot(0).isEmpty()) {
            ItemStack stack = this.stack.copy();
            stack.setCount(Math.min(stack.getMaxStackSize(), amount));
            amount -= stack.getCount();
            ItemHandlerHelper.insertItem(outItems, stack, false);
        } else if (outItems.getStackInSlot(0).getCount() < outItems.getStackInSlot(0).getMaxStackSize()) {
            ItemStack stack = outItems.getStackInSlot(0);
            int increment = Math.min(amount, 64 - stack.getCount());
            stack.setCount(stack.getCount() + increment);
            amount -= increment;
        }
        if (amount == 0 && outItems.getStackInSlot(0).isEmpty()) {
            stack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inItems = new ItemStackHandler(1);
        this.addInventory(new ColoredItemHandler(inItems, EnumDyeColor.BLUE, "Input items", new BoundingRectangle(18, 25, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return BlackHoleUnitTile.this.canInsertItem(stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, box.getLeft() + 1, box.getTop() + 1));
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18, 1, 1, BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.BLUE));
                return pieces;
            }

            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
                super.setStackInSlot(slot, stack);
            }
        });
        this.addInventoryToStorage(inItems, "block_hole_in");
        outItems = new ItemStackHandler(1);
        this.addInventory(new ColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output items", new BoundingRectangle(18, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, box.getLeft() + 1, box.getTop() + 1));
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18, 1, 1, BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.ORANGE));
                return pieces;
            }

            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
                super.setStackInSlot(slot, stack);
            }

        });
        this.addInventoryToStorage(outItems, "black_hole_out");
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> list = super.getGuiContainerPieces(container);
        list.add(new BlackHoleInfoPiece(this, 18 * 2 + 8, 25));
        return list;
    }

    @Override
    protected void createAddonsInventory() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setString(NBT_ITEMSTACK, stack.getItem().getRegistryName().toString());
        tagCompound.setInteger(NBT_AMOUNT, amount);
        tagCompound.setInteger(NBT_META, stack.getMetadata());
        tagCompound.setTag(NBT_ITEM_NBT,stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_ITEMSTACK)) stack = ItemStack.EMPTY;
        else {
            Item item = Item.getByNameOrId(compound.getString(NBT_ITEMSTACK));
            if (item != null) {
                stack = new ItemStack(item, 1, compound.hasKey(NBT_META) ? compound.getInteger(NBT_META) : 0);
                if (compound.hasKey(NBT_ITEM_NBT)) stack.setTagCompound(compound.getCompoundTag(NBT_ITEM_NBT));
            }
        }
        if (!compound.hasKey(NBT_AMOUNT)) amount = 0;
        else {
            amount = compound.getInteger(NBT_AMOUNT);
        }
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean canInsertItem(ItemStack stack) {
        return Integer.MAX_VALUE >= stack.getCount() + amount && (BlackHoleUnitTile.this.stack.isEmpty() || (stack.getItem() == BlackHoleUnitTile.this.stack.getItem() && stack.getMetadata() == BlackHoleUnitTile.this.stack.getMetadata() && (!(stack.hasTagCompound() && BlackHoleUnitTile.this.stack.hasTagCompound()) || stack.getTagCompound().equals(BlackHoleUnitTile.this.stack.getTagCompound()))));
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getAmount() {
        return amount + (outItems.getStackInSlot(0).isEmpty() ? 0 : outItems.getStackInSlot(0).getCount());
    }

    private BlackHoleHandler itemHandler = new BlackHoleHandler(this);

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }

    private class BlackHoleHandler implements IItemHandler {

        private BlackHoleUnitTile tile;

        public BlackHoleHandler(BlackHoleUnitTile tile) {
            this.tile = tile;
        }

        @Override
        public int getSlots() {
            return tile.getAmount() / tile.getStack().getMaxStackSize() + 2;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            double stacks = tile.getAmount() / (double)tile.getStack().getMaxStackSize();
            if (getAmount() <= tile.getStack().getMaxStackSize() && slot == 0) return outItems.getStackInSlot(0);
            ItemStack stack = tile.stack.copy();
            stack.setCount(slot < (int) stacks ? tile.getStack().getMaxStackSize() : slot == (int) stacks ? (int) ((stacks - tile.getAmount() / tile.getStack().getMaxStackSize()) * tile.getStack().getMaxStackSize()) : 0);
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (tile.canInsertItem(stack)) return inItems.insertItem(slot, stack, simulate);
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return outItems.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    }
}
