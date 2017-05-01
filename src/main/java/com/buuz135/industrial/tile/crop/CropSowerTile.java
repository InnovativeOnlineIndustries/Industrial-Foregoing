package com.buuz135.industrial.tile.crop;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockFarmland;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class CropSowerTile extends WorkingAreaElectricMachine {

    private ItemStackHandler inPlant;
    private IFluidTank waterTank;

    private FakePlayer fakePlayer;

    public CropSowerTile() {
        super(CropSowerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        waterTank = this.addFluidTank(FluidRegistry.WATER, 8000, EnumDyeColor.BLUE, "Water tank", new BoundingRectangle(50, 25, 18, 54));
        inPlant = new ItemStackHandler(12);
        this.addInventory(new ColoredItemHandler(inPlant, EnumDyeColor.GREEN, "Seeds input", new BoundingRectangle(18 * 5 + 3, 25, 18 * 4, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemSeeds || stack.getItem() instanceof ItemSeedFood;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                int i = 0;
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 4; x++) {
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
                        4, 3,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));

                return pieces;
            }
        });
        this.addInventoryToStorage(inPlant, "crop_sower_in");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        int r = 1;
        BlockPos corner1 = new BlockPos(0, 2, 0);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(r, 0, r);
    }

    @Override
    protected float performWork() {
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        for (BlockPos pos : blockPos) {
            if (this.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.DIRT)) {
                this.world.setBlockState(pos.add(0, -1, 0), Blocks.FARMLAND.getDefaultState());
                return 1;
            }
            if (this.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.FARMLAND) && this.world.getBlockState(pos.add(0, -1, 0)).getValue(BlockFarmland.MOISTURE) <= 6 && waterTank.getFluidAmount() > 50) {
                this.world.setBlockState(pos.add(0, -1, 0), this.world.getBlockState(pos.add(0, -1, 0)).withProperty(BlockFarmland.MOISTURE, 7));
                waterTank.drain(50, true);
                return 1;
            }
            if (this.world.isAirBlock(pos)) {
                FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                ItemStack stack = getFirstItem();
                if (!stack.isEmpty()) {
                    Item seeds = stack.getItem();
                    player.setHeldItem(EnumHand.MAIN_HAND, stack);
                    seeds.onItemUse(player, world, pos.offset(EnumFacing.DOWN), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
                    return 1;
                }
            }
        }
        return 1;
    }

    private ItemStack getFirstItem() {
        for (int i = 0; i < inPlant.getSlots(); ++i)
            if (!inPlant.getStackInSlot(i).isEmpty()) return inPlant.getStackInSlot(i);
        return ItemStack.EMPTY;
    }

    @Override
    protected int getEnergyForWorkRate() {
        return 20;
    }

    @Override
    protected int getMinimumWorkTicks() {
        return 10;
    }

    @Override
    protected int getEnergyForWork() {
        return 100;
    }

    @Override
    public long getWorkEnergyCapacity() {
        return 100;
    }
}
