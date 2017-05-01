package com.buuz135.industrial.tile.crop;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockCrops;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class CropEnrichMaterialInjectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";

    private ItemStackHandler inFert;
    private int pointer;


    public CropEnrichMaterialInjectorTile() {
        super(CropEnrichMaterialInjectorTile.class.getName().hashCode());
        pointer = 0;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inFert = new ItemStackHandler(12);
        this.addInventory(new ColoredItemHandler(inFert, EnumDyeColor.GREEN, "Fertilizer input", new BoundingRectangle(18 * 5 + 3, 25, 18 * 4, 18 * 3)) {
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
        this.addInventoryToStorage(inFert, "crop_fert_in");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        int r = 1;
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(this.getFacing().getOpposite(), r + 1);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(r, 0, r);
    }

    @Override
    protected float performWork() {
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        if (pointer < blockPos.size()) {
            if (this.world.getBlockState(blockPos.get(pointer)).getBlock() instanceof BlockCrops) {
                ItemStack stack = getFirstItem();
                if (!stack.isEmpty()) {
                    FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                    ItemDye.applyBonemeal(stack, this.world, blockPos.get(pointer), player, EnumHand.MAIN_HAND);
                    ++pointer;
                    if (pointer >= blockPos.size()) pointer = 0;
                    return 1;
                }
            }
        } else {
            pointer = 0;
        }
        return 1;
    }

    private ItemStack getFirstItem() {
        for (int i = 0; i < inFert.getSlots(); ++i)
            if (!inFert.getStackInSlot(i).isEmpty()) return inFert.getStackInSlot(i);
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_POINTER, pointer);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_POINTER)) pointer = 0;
        else pointer = compound.getInteger(NBT_POINTER);
    }

}
