package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.client.infopiece.BlackHoleInfoPiece;
import com.buuz135.industrial.proxy.client.infopiece.IHasDisplayStack;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;
import net.ndrei.teslacorelib.items.TeslaWrench;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class BlackHoleUnitTile extends CustomSidedTileEntity implements IHasDisplayStack {

    public static final String NBT_ITEMSTACK = "itemstack";
    public static final String NBT_AMOUNT = "amount";
    public static final String NBT_META = "meta";
    public static final String NBT_ITEM_NBT = "stack_nbt";
    private LockableItemHandler inItems;
    private LockableItemHandler outItems;
    private ItemStack stack;
    private int amount;
    private BlackHoleHandler itemHandler = new BlackHoleHandler(this);

    public BlackHoleUnitTile() {
        super(BlackHoleUnitTile.class.getName().hashCode());
        stack = ItemStack.EMPTY;
        amount = 0;
    }

    @Override
    protected void innerUpdate() {
        if (WorkUtils.isDisabled(this.getBlockType())) return;
        inItems.setLocked(outItems.getLocked());
        inItems.setFilter(outItems.getFilter());
        if (outItems.getStackInSlot(0).isEmpty()) {
            ItemStack stack = this.stack.copy();
            stack.setCount(Math.min(stack.getMaxStackSize(), amount));
            amount -= stack.getCount();
            ItemHandlerHelper.insertItem(outItems, stack, false);
        } else if (outItems.getStackInSlot(0).getCount() <= outItems.getStackInSlot(0).getMaxStackSize()) {
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
        inItems = new LockableItemHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemStack in = inItems.getStackInSlot(0);
                if (stack.isEmpty()) {
                    stack = in;
                    amount = 0;
                }
                amount += in.getCount();
                inItems.setStackInSlot(0, ItemStack.EMPTY);
                BlackHoleUnitTile.this.partialSync(NBT_AMOUNT, true);
            }
        };
        this.addInventory(new ColoredItemHandler(inItems, EnumDyeColor.BLUE, "Input items", new BoundingRectangle(16, 25, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return BlackHoleUnitTile.this.canInsertItem(stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
                super.setStackInSlot(slot, stack);
            }
        });
        this.addInventoryToStorage(inItems, "block_hole_in");
        outItems = new LockableItemHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                BlackHoleUnitTile.this.partialSync(NBT_AMOUNT, true);
            }
        };
        this.addInventory(new ColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output items", new BoundingRectangle(16, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
                super.setStackInSlot(slot, stack);
            }

        });
        this.addInventoryToStorage(outItems, "black_hole_out");
        this.registerSyncIntPart(NBT_AMOUNT, nbtTagInt -> amount = nbtTagInt.getInt(), () -> new NBTTagInt(amount), SyncProviderLevel.GUI);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> list = super.getGuiContainerPieces(container);
        list.add(new LockedInventoryTogglePiece(18 * 2 + 9, 83, this, EnumDyeColor.ORANGE));
        list.add(new BlackHoleInfoPiece(this, 18 * 2 + 8, 25));
        return list;
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setString(NBT_ITEMSTACK, stack.getItem().getRegistryName().toString());
        tagCompound.setInteger(NBT_AMOUNT, amount);
        tagCompound.setInteger(NBT_META, stack.getMetadata());
        tagCompound.setTag(NBT_ITEM_NBT, stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_ITEMSTACK)) stack = ItemStack.EMPTY;
        else {
            Item item = Item.getByNameOrId(compound.getString(NBT_ITEMSTACK));
            if (item != null) {
                stack = new ItemStack(item, 1, compound.getInteger(NBT_META));
                NBTTagCompound nbttag = compound.getCompoundTag(NBT_ITEM_NBT);
                if (!nbttag.hasNoTags()) stack.setTagCompound(nbttag);
            }
        }
        if (!compound.hasKey(NBT_AMOUNT)) amount = 0;
        else {
            amount = compound.getInteger(NBT_AMOUNT);
        }
    }

    public boolean canInsertItem(ItemStack stack) {
        return Integer.MAX_VALUE >= stack.getCount() + amount && (BlackHoleUnitTile.this.stack.isEmpty() || (stack.isItemEqual(this.stack) && (!(stack.hasTagCompound() && this.stack.hasTagCompound()) || stack.getTagCompound().equals(BlackHoleUnitTile.this.stack.getTagCompound()))));
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }


    public ItemStack getItemStack() {
        return stack;
    }

    public int getAmount() {
        return amount + (outItems.getStackInSlot(0).isEmpty() ? 0 : outItems.getStackInSlot(0).getCount());
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDisplayNameUnlocalized() {
        return getItemStack().getUnlocalizedName().endsWith(".name") ? getItemStack().getUnlocalizedName() : getItemStack().getUnlocalizedName() + ".name";
    }

    @NotNull
    @Override
    public EnumActionResult onWrenchUse(@NotNull TeslaWrench wrench, @NotNull EntityPlayer player, @NotNull World world, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == null) return false;
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

    @Override
    public boolean getAllowRedstoneControl() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

    private class BlackHoleHandler implements IItemHandler {

        private BlackHoleUnitTile tile;

        public BlackHoleHandler(BlackHoleUnitTile tile) {
            this.tile = tile;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemStack stack = tile.getItemStack().copy();
            stack.setCount(tile.getAmount());
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (tile.canInsertItem(stack)) {
                return inItems.insertItem(0, stack, simulate);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) return ItemStack.EMPTY;
            ItemStack existing = tile.getItemStack().copy();
            if (existing.isEmpty()) return ItemStack.EMPTY;
            if (tile.getAmount() <= amount) {
                int newAmount = tile.getAmount();
                if (!simulate) {
                    tile.setAmount(0);
                    outItems.setStackInSlot(0, ItemStack.EMPTY);
                }
                return ItemHandlerHelper.copyStackWithSize(existing, newAmount);
            } else {
                if (!simulate) {
                    tile.setAmount(tile.amount - amount);
                }
                return ItemHandlerHelper.copyStackWithSize(existing, amount);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }
    }
}
