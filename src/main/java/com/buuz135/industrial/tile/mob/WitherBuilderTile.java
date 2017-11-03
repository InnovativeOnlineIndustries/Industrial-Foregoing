package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import org.jetbrains.annotations.NotNull;

public class WitherBuilderTile extends WorkingAreaElectricMachine {

    private ItemStackHandler top;
    private ItemStackHandler middle;
    private ItemStackHandler bottom;

    public WitherBuilderTile() {
        super(WitherBuilderTile.class.getName().hashCode(), 0, 0, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        int left = 75;
        top = new ItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                WitherBuilderTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(top, EnumDyeColor.BLACK, "wither_skulls", new BoundingRectangle(left, 25, 18 * 3, 18)) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.isItemEqual(new ItemStack(Items.SKULL, 1, 1));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(top, "wither_skulls");
        middle = new ItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                WitherBuilderTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(middle, EnumDyeColor.BROWN, "soulsand", new BoundingRectangle(left, 25 + 18, 18 * 3, 18)) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.isItemEqual(new ItemStack(Blocks.SOUL_SAND));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(middle, "soulsand_middle");
        bottom = new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                WitherBuilderTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(bottom, EnumDyeColor.BROWN, "soulsand", new BoundingRectangle(left + 18, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.isItemEqual(new ItemStack(Blocks.SOUL_SAND));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(middle, "soulsand_bottom");
    }

    @Override
    public float work() {
        BlockPos pos = this.pos.add(0, 2, 0);
        float power = 0;
        if (this.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && !bottom.getStackInSlot(0).isEmpty()) {
            this.world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
            bottom.getStackInSlot(0).shrink(1);
            power += 1 / 7f;
        }
        if (this.world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
            for (int i = 0; i < 3; ++i) {
                BlockPos temp = pos.add(i - 1, 1, 0);
                if (this.world.getBlockState(temp).getBlock().equals(Blocks.AIR) && !middle.getStackInSlot(i).isEmpty()) {
                    this.world.setBlockState(temp, Blocks.SOUL_SAND.getDefaultState());
                    middle.getStackInSlot(i).shrink(1);
                    power += 1 / 7f;
                }
            }
        }
        if (this.world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
            boolean secondRow = true;
            for (int i = 0; i < 3; ++i) {
                BlockPos temp = pos.add(i - 1, 1, 0);
                if (!this.world.getBlockState(temp).getBlock().equals(Blocks.SOUL_SAND)) {
                    secondRow = false;
                    break;
                }
            }
            if (secondRow) {
                for (int i = 0; i < 3; ++i) {
                    BlockPos temp = pos.add(i - 1, 2, 0);
                    if (this.world.getBlockState(temp).getBlock().equals(Blocks.AIR) && !top.getStackInSlot(i).isEmpty()) {
                        FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                        player.setHeldItem(EnumHand.MAIN_HAND, top.getStackInSlot(i));
                        top.getStackInSlot(i).onItemUse(player, world, temp.offset(EnumFacing.DOWN), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
                        power += 1 / 7f;
                    }
                }
            }
        }
        return power;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(1, 1, 0).offset(new BlockPos(0, 3, 0));
    }
}
