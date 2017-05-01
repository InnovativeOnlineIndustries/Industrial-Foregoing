package com.buuz135.industrial.tile.animal;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
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
        super(WaterResourcesCollectorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outFish = new ItemStackHandler(3 * 6);
        this.addInventory(new ColoredItemHandler(outFish, EnumDyeColor.GREEN, "Fish output", new BoundingRectangle(18 * 3, 25, 18 * 6, 18 * 3)) {
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
                int i = 0;
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 6; x++) {
                        slots.add(new FilteredSlot(this.getItemHandlerForContainer(), i, box.getLeft() + 1 + x * 18, box.getTop() + 1 + y * 18));
                        ++i;
                    }
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        6, 3,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));

                return pieces;
            }
        });
        this.addInventoryToStorage(outFish, "water_resource_collector_out");
    }

    @Override
    protected float performWork() {
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
        int r = 1;
        BlockPos corner1 = new BlockPos(0, -1, 0);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(r, 0, r);
    }

    @Override
    protected int getEnergyForWork() {
        return 2000;
    }

    @Override
    protected int getEnergyForWorkRate() {
        return 20;
    }
}
