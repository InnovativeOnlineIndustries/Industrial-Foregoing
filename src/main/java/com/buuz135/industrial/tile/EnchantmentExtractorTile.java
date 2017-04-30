package com.buuz135.industrial.tile;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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


public class EnchantmentExtractorTile extends ElectricMachine {

    private ItemStackHandler inBook;
    private ItemStackHandler inEnchanted;
    private ItemStackHandler outItem;
    private ItemStackHandler outEnchanted;

    public EnchantmentExtractorTile() {
        super(EnchantmentExtractorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        initInputInv();
        initOutputInv();
    }

    private void initInputInv() {
        inBook = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentExtractorTile.this.markDirty();
            }
        };

        this.addInventory(new ColoredItemHandler(this.inBook, EnumDyeColor.BROWN, "Input books", new BoundingRectangle(18 * 3, 25, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.BOOK);
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
                TiledRenderedGuiPiece piece = new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        1, this.getItemHandlerForContainer().getSlots(),
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.BROWN);

                pieces.add(piece);

                return pieces;
            }


        });
        this.addInventoryToStorage(this.inBook, "ench_ext_in_books");

        inEnchanted = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentExtractorTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(this.inEnchanted, EnumDyeColor.PURPLE, "Input enchanted items", new BoundingRectangle(18 * 3, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.isItemEnchanted();
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
        this.addInventoryToStorage(this.inEnchanted, "ench_ext_in_items");
    }


    private void initOutputInv() {
        outEnchanted = new ItemStackHandler(4);
        this.addInventory(new ColoredItemHandler(outEnchanted, EnumDyeColor.MAGENTA, "Enchanted Books", new BoundingRectangle(18 * 4 + 14, 25, 18 * 4, 18)) {
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
        this.addInventoryToStorage(outEnchanted, "ench_ext_out_book");

        outItem = new ItemStackHandler(4);
        this.addInventory(new ColoredItemHandler(outItem, EnumDyeColor.YELLOW, "Enchantless Items", new BoundingRectangle(18 * 4 + 14, 25 + 18 * 2, 18 * 4, 18)) {
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
        this.addInventoryToStorage(outItem, "ench_ext_out_items");
    }

    private boolean hasBooks() {
        return !inBook.getStackInSlot(0).isEmpty();
    }

    private ItemStack getItem() {
        return inEnchanted.getStackInSlot(0);
    }

    @Override
    protected float performWork() {
        if (!hasBooks() || getItem().isEmpty()) return 0;
        ItemStack enchantedItem = getItem();
        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        if (ItemHandlerHelper.insertItem(outEnchanted, enchantedBook, true).isEmpty() && ItemHandlerHelper.insertItem(outItem, enchantedItem, true).isEmpty()) {
            NBTTagCompound base = (NBTTagCompound) enchantedItem.getEnchantmentTagList().get(0);
            enchantedBook.addEnchantment(Enchantment.getEnchantmentByID(base.getShort("id")), base.getShort("lvl"));
            enchantedItem.getEnchantmentTagList().removeTag(0);
            ItemHandlerHelper.insertItem(outEnchanted, enchantedBook, false);
            ItemHandlerHelper.insertItem(outItem, enchantedItem.copy(), false);
            inBook.getStackInSlot(0).setCount(inBook.getStackInSlot(0).getCount() - 1);
            enchantedItem.setCount(enchantedItem.getCount() - 1);
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
