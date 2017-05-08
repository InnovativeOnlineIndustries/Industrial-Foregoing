package com.buuz135.industrial.tile.magic;


import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class EnchantmentAplicatorTile extends CustomElectricMachine {

    private ItemStackHandler inEnchantedBook;
    private ItemStackHandler inItem;
    private ItemStackHandler outEnchantedItem;
    private IFluidTank experienceTank;

    public EnchantmentAplicatorTile() {
        super(EnchantmentAplicatorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        this.initInputInv();
        this.initOutputInv();
    }

    private void initInputInv() {
        this.experienceTank = this.addFluidTank(FluidsRegistry.ESSENCE, 32000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(50, 25, 18, 54));

        inEnchantedBook = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(this.inEnchantedBook, EnumDyeColor.PURPLE, "Input enchanted books", new BoundingRectangle(18 * 5 + 3, 25, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.ENCHANTED_BOOK);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                for (int x = 0; x < this.handler.getSlots(); x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1, box.getTop() + 1 + x * 18));
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        1, this.getItemHandlerForContainer().getSlots(),
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE));

                return pieces;
            }


        });
        this.addInventoryToStorage(this.inEnchantedBook, "ench_aplic_in_book");

        inItem = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(this.inItem, EnumDyeColor.GREEN, "Input items", new BoundingRectangle(18 * 7 + 3, 25, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                for (int x = 0; x < this.handler.getSlots(); x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1, box.getTop() + 1 + x * 18));
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        1, this.getItemHandlerForContainer().getSlots(),
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));
                return pieces;
            }
        });
        this.addInventoryToStorage(this.inItem, "ench_aplic_in_item");

    }

    private void initOutputInv() {
        outEnchantedItem = new ItemStackHandler(3);
        this.addInventory(new ColoredItemHandler(outEnchantedItem, EnumDyeColor.MAGENTA, "Enchanted items", new BoundingRectangle(18 * 5 + 3, 25 + 18 * 2, 18 * 3, 18)) {
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
                for (int x = 0; x < this.handler.getSlots(); x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1 + x * 18, box.getTop() + 1));
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        this.getItemHandlerForContainer().getSlots(), 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.MAGENTA));

                return pieces;
            }
        });
        this.addInventoryToStorage(outEnchantedItem, "ench_aplic_out_item");

    }

    public double getLevels() {
        NBTTagList list = ((ItemEnchantedBook) inEnchantedBook.getStackInSlot(0).getItem()).getEnchantments(inEnchantedBook.getStackInSlot(0));
        double amount = list.tagCount();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound compound = ((NBTTagCompound) list.get(i));
            //Enchantment enchantment = Enchantment.getEnchantmentByID()
            amount *= (1 + compound.getShort("lvl") / 10D);
        }
        if (inItem.getStackInSlot(0).isItemEnchanted()) {
            list = inItem.getStackInSlot(0).getEnchantmentTagList();
            amount *= list.tagCount();
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound compound = ((NBTTagCompound) list.get(i));
                //Enchantment enchantment = Enchantment.getEnchantmentByID()
                amount *= (1 + compound.getShort("lvl") / 10D);
            }
        }
        return amount;
    }

    public boolean canWork() {
        return !inItem.getStackInSlot(0).isEmpty() && !inEnchantedBook.getStackInSlot(0).isEmpty();
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;
        if (!canWork()) return 0;
        int xp = (int) (getLevels() * 1000);
        if (experienceTank.getFluidAmount() >= xp && ItemHandlerHelper.insertItem(outEnchantedItem, inItem.getStackInSlot(0), true).isEmpty()) {
            NBTTagList list = ((ItemEnchantedBook) inEnchantedBook.getStackInSlot(0).getItem()).getEnchantments(inEnchantedBook.getStackInSlot(0));
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound compound = ((NBTTagCompound) list.get(i));
                Enchantment enchantment = Enchantment.getEnchantmentByID(compound.getShort("id"));
                ItemStack stack = inItem.getStackInSlot(0).copy();
                stack.addEnchantment(enchantment, compound.getShort("lvl"));
                ItemHandlerHelper.insertItem(outEnchantedItem, stack, false);

                inItem.getStackInSlot(0).setCount(0);
                inEnchantedBook.getStackInSlot(0).setCount(0);
                experienceTank.drain(xp, true);
                System.out.println(compound);
                return 500;
            }
        }
        return 0;
    }

    @Override
    protected int getEnergyForWork() {
        return 2000;
    }

    @Override
    public long getWorkEnergyCapacity() {
        return 2000;
    }

    @Override
    public long getWorkEnergyTick() {
        return 50;
    }
}
