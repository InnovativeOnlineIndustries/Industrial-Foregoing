package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Slot;
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
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class WaterResourcesCollectorTile extends WorkingAreaElectricMachine {

    private ItemStackHandler outFish;

    public WaterResourcesCollectorTile() {
        super(WaterResourcesCollectorTile.class.getName().hashCode(), 1, 0);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outFish = new ItemStackHandler(3 * 6){
            @Override
            protected void onContentsChanged(int slot) {
                WaterResourcesCollectorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outFish, EnumDyeColor.GREEN, "Fish output", 18 * 3, 25, 6,  3) {
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
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;
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
                ItemHandlerHelper.insertItem(outFish, stack, false);
            }
            return 1;
        }
        return 1;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        BlockPos corner1 = new BlockPos(0, -1, 0);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(getRadius(), 0, getRadius());
    }

    @Override
    public boolean canAcceptRangeUpgrades() {
        return false;
    }
}
