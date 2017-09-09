package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class OreProcessorTile extends CustomElectricMachine {

    private ItemStackHandler input;
    private IFluidTank tank;
    private ItemStackHandler output;

    public OreProcessorTile() {
        super(OreProcessorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                OreProcessorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(input, EnumDyeColor.BLUE, "Ores input", 18 * 2 + 12, 25, 1, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if (ItemStackUtils.isOre(stack)) {
                    if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
                        Block block = Block.getBlockFromItem(stack.getItem());
                        NonNullList<ItemStack> stacks = NonNullList.create();
                        block.getDrops(stacks, OreProcessorTile.this.world, OreProcessorTile.this.pos, block.getStateFromMeta(stack.getMetadata()), 0);
                        if (stacks.size() > 0 && !stacks.get(0).isItemEqual(stack)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(input, "input");
        tank = this.addFluidTank(FluidsRegistry.ESSENCE, 8000, EnumDyeColor.GREEN, "Essence tank", new BoundingRectangle(18 * 4 - 2, 25, 18, 54));
        output = new ItemStackHandler(3 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                OreProcessorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(output, EnumDyeColor.ORANGE, "Processed ores output", 18 * 6 + 2, 25, 3, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(output, "outout");
    }

    private ItemStack getFirstStack() {
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!input.getStackInSlot(i).isEmpty()) return input.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        ItemStack stack = getFirstStack();
        if (stack.isEmpty()) return 0;
        Block block = Block.getBlockFromItem(stack.getItem());
        int fortune = getFortuneLevel();
        tank.drain(fortune * BlockRegistry.oreProcessorBlock.getEssenceFortune(), true);
        NonNullList<ItemStack> stacks = NonNullList.create();
        block.getDrops(stacks, OreProcessorTile.this.world, OreProcessorTile.this.pos, block.getStateFromMeta(stack.getMetadata()), fortune);
        boolean canInsert = true;
        for (ItemStack temp : stacks) {
            if (!ItemHandlerHelper.insertItem(output, temp, true).isEmpty()) {
                canInsert = false;
                break;
            }
        }
        if (canInsert) {
            for (ItemStack temp : stacks) {
                ItemHandlerHelper.insertItem(output, temp, false);
            }
            stack.setCount(stack.getCount() - 1);
            return 1;
        }
        return 0;
    }

    private int getFortuneLevel() {
        for (int i = 3; i > 0; i--) {
            if (i * BlockRegistry.oreProcessorBlock.getEssenceFortune() <= tank.getFluidAmount()) return i;
        }
        return 0;
    }
}
