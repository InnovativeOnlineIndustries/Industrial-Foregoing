package com.buuz135.industrial.tile.magic;

import com.buuz135.industrial.tile.CustomElectricMachine;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;

import java.util.List;

public class EnchantmentRefinerTile extends CustomElectricMachine {

    private ItemStackHandler input;
    private ItemStackHandler outputNoEnch;
    private ItemStackHandler outputEnch;


    public EnchantmentRefinerTile() {
        super(EnchantmentRefinerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.initInputInv();
        this.initOutputInv();
    }

    private void initInputInv() {
        input = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentRefinerTile.this.markDirty();
            }
        };

        this.addInventory(new ColoredItemHandler(this.input, EnumDyeColor.GREEN, "Input items", this.getInputInventoryBounds(1, 3)) {
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
        this.addInventoryToStorage(this.input, "ench_ref_in");

    }

    protected BoundingRectangle getInputInventoryBounds(int columns, int rows) {
        return new BoundingRectangle(18 * 3, 25, 18 * columns, 18 * rows);
    }

    private void initOutputInv() {
        outputEnch = new ItemStackHandler(4);
        this.addInventory(new ColoredItemHandler(outputEnch, EnumDyeColor.PURPLE, "Enchanted Items", new BoundingRectangle(18 * 4 + 14, 25, 18 * 4, 18)) {
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
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE));

                return pieces;
            }
        });
        this.addInventoryToStorage(outputEnch, "ench_ref_out_yes");

        outputNoEnch = new ItemStackHandler(4);
        this.addInventory(new ColoredItemHandler(outputNoEnch, EnumDyeColor.YELLOW, "No enchanted Items", new BoundingRectangle(18 * 4 + 14, 25 + 18 * 2, 18 * 4, 18)) {
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
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.YELLOW));

                return pieces;
            }
        });
        this.addInventoryToStorage(outputNoEnch, "ench_ref_out_no");
    }

    public ItemStack getFirstItem() {
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!input.getStackInSlot(i).isEmpty()) {
                return input.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected float performWork() {
        ItemStack stack = getFirstItem();
        if (stack.isEmpty()) {
            return 0;
        }
        ItemStack out = stack.copy();
        out.setCount(1);
        if (stack.isItemEnchanted() || stack.getItem().equals(Items.ENCHANTED_BOOK)) {
            if (ItemHandlerHelper.insertItem(outputEnch, out, true).isEmpty()) {
                ItemHandlerHelper.insertItem(outputEnch, out, false);
                stack.setCount(stack.getCount() - 1);
                return 500;
            }
        } else if (ItemHandlerHelper.insertItem(outputNoEnch, out, true).isEmpty()) {
            ItemHandlerHelper.insertItem(outputNoEnch, out, false);
            stack.setCount(stack.getCount() - 1);
            return 500;
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

    @Override
    public boolean supportsSpeedUpgrades() {
        return true;
    }

    @Override
    public boolean supportsEnergyUpgrades() {
        return true;
    }
}
