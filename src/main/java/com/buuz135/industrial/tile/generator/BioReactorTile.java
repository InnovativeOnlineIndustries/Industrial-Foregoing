package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.client.infopiece.BioreactorEfficiencyInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.tile.block.BioReactorBlock;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;

public class BioReactorTile extends CustomElectricMachine {

    private ItemStackHandler input;
    private ItemStackHandler buffer;
    private IFluidTank tank;

    public BioReactorTile() {
        super(BioReactorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.BIOFUEL, 8000, EnumDyeColor.PURPLE, "Biofuel tank", new BoundingRectangle(44, 25, 18, 54));
        input = new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                BioReactorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(input, EnumDyeColor.BLUE, "Input items", 18 * 4 + 10, 25, 3, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return ((BioReactorBlock) BioReactorTile.this.getBlockType()).getItemsAccepted().stream().anyMatch(stack1 -> stack.getItem().equals(stack1.getItem())) && !alreadyContains(input, stack, 64);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(input, "input");
        buffer = new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                BioReactorTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 16;
            }
        };
        this.addInventory(new CustomColoredItemHandler(buffer, EnumDyeColor.YELLOW, "Buffer", 18 * 7 + 12, 25, 3, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(buffer, "buffer");
    }

    @Override
    protected void createAddonsInventory() {

    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BioreactorEfficiencyInfoPiece(this, 117, 83));
        return pieces;
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        for (int i = 0; i < buffer.getSlots() - 1; ++i) {
            if (buffer.getStackInSlot(i).isEmpty()) {
                int tempI = i;
                while (tempI < buffer.getSlots()) {
                    if (!buffer.getStackInSlot(tempI).isEmpty()) {
                        buffer.setStackInSlot(i, buffer.getStackInSlot(tempI).copy());
                        buffer.setStackInSlot(tempI, ItemStack.EMPTY);
                        break;
                    }
                    ++tempI;
                }
            }
        }
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!alreadyContains(buffer, input.getStackInSlot(i), 16)) {
                ItemStack out = input.getStackInSlot(i).copy();
                out.setCount(1);
                if (ItemHandlerHelper.insertItem(buffer, out, true).isEmpty()) {
                    ItemHandlerHelper.insertItem(buffer, out, false);
                    input.getStackInSlot(i).setCount(input.getStackInSlot(i).getCount() - 1);
                }
            }
        }
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock) this.getBlockType()).isWorkDisabled()) return 0;

        if (getEfficiency() < 0) return 0;
        FluidStack stack = new FluidStack(FluidsRegistry.BIOFUEL, getProducedAmountItem() * getItemAmount());
        if (tank.getFluid() == null || (stack.amount + tank.getFluidAmount() <= tank.getCapacity())) {
            tank.fill(stack, true);
            for (int i = 0; i < buffer.getSlots(); ++i) {
                if (!buffer.getStackInSlot(i).isEmpty())
                    buffer.getStackInSlot(i).setCount(buffer.getStackInSlot(i).getCount() - 1);
            }
            return 1;
        }
        return 0;
    }

    private boolean alreadyContains(ItemStackHandler handler, ItemStack stack, int amountAtleast) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (stack.getItem().equals(handler.getStackInSlot(i).getItem()) && stack.getMetadata() == handler.getStackInSlot(i).getMetadata() && handler.getStackInSlot(i).getCount() >= amountAtleast)
                return true;
        }
        return false;
    }


    public int getItemAmount() {
        int am = 0;
        for (int i = 0; i < buffer.getSlots(); ++i) {
            if (!buffer.getStackInSlot(i).isEmpty()) ++am;
        }
        return am;
    }

    public float getEfficiency() {
        return (getItemAmount() - 1) / 8f;
    }

    public int getProducedAmountItem() {
        float eff = getEfficiency();
        if (eff < 0) return 0;
        int base = ((BioReactorBlock) this.getBlockType()).getBaseAmount();
        return (int) (getEfficiency() * base + base);
    }
}
