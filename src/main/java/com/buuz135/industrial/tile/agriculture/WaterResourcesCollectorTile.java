package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class WaterResourcesCollectorTile extends WorkingAreaElectricMachine {

    private ItemStackHandler outFish;

    public WaterResourcesCollectorTile() {
        super(WaterResourcesCollectorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outFish = new ItemStackHandler(3 * 6) {
            @Override
            protected void onContentsChanged(int slot) {
                WaterResourcesCollectorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outFish, EnumDyeColor.GREEN, "Fish output", 18 * 3, 25, 6, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outFish, "outFish");
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (this.world.rand.nextBoolean() && this.world.rand.nextBoolean()) return 1;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        boolean allWaterSources = true;
        for (BlockPos pos : blockPos) {
            IBlockState state = this.world.getBlockState(pos);
            if (!(state.getBlock().equals(FluidRegistry.WATER.getBlock()) && state.getBlock().getMetaFromState(state) == 0))
                allWaterSources = false;
        }
        if (allWaterSources) {
            LootContext.Builder lootcontext = new LootContext.Builder((WorldServer) this.world);
            List<ItemStack> items = this.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.world.rand, lootcontext.build());
            for (ItemStack stack : items) {
                ItemHandlerHelper.insertItem(outFish, stack.copy(), false);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return super.getWorkingArea().offset(0, -2, 0);
    }

}
