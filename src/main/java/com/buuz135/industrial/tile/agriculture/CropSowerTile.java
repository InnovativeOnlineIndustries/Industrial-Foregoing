package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;

import java.util.List;

public class CropSowerTile extends WorkingAreaElectricMachine {

    private static final String NBT_POINTER = "pointer";

    public static EnumDyeColor[] colors = new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.CYAN, EnumDyeColor.PURPLE, EnumDyeColor.YELLOW, EnumDyeColor.WHITE, EnumDyeColor.MAGENTA, EnumDyeColor.LIME, EnumDyeColor.BLUE, EnumDyeColor.BLACK};

    private ItemStackHandler inPlant;
    private int pointer;

    public CropSowerTile() {
        super(CropSowerTile.class.getName().hashCode(), 1, 1, true);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inPlant = new LockableItemHandler(3 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                CropSowerTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 16;
            }
        };
        this.addInventory(new ColoredItemHandler(inPlant, EnumDyeColor.GREEN, "Seeds input", new BoundingRectangle(18 * 3, 25, 18 * 3, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return super.canInsertItem(slot, stack) && (stack.getItem() instanceof IPlantable || ItemStackUtils.isStackOreDict(stack, "treeSapling"));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
                List<IGuiContainerPiece> guiContainerPieces = super.getGuiContainerPieces(container);
                int i = 0;
                for (int x = 0; x < 3; ++x) {
                    for (int y = 0; y < 3; ++y) {
                        guiContainerPieces.add(new TiledRenderedGuiPiece(18 * 3 + 18 * x, 25 + 18 * y, 18, 18, 1, 1, BasicTeslaGuiContainer.Companion.getMACHINE_BACKGROUND(), 108, 225, colors[i]));
                        ++i;
                    }
                }
                return guiContainerPieces;
            }
        });
        this.addInventoryToStorage(inPlant, "inPlant");

    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        BlockPos corner1 = new BlockPos(0, 2, 0);
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(corner1).grow(getRadius(), 0, getRadius());
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        ++pointer;
        if (pointer >= blockPos.size()) pointer = 0;
        if (pointer < blockPos.size()) {
            BlockPos pos = blockPos.get(pointer);
            if (this.world.isAirBlock(pos)) {
                FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                ItemStack stack = inPlant.getStackInSlot(getFilteredSlot(pos));
                if (!stack.isEmpty()) {
                    Item seeds = stack.getItem();
                    player.setHeldItem(EnumHand.MAIN_HAND, stack);
                    if (!ItemStackUtils.isStackOreDict(stack, "treeSapling") && (this.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().equals(Blocks.DIRT) || this.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().equals(Blocks.GRASS))) {
                        this.world.setBlockState(pos.offset(EnumFacing.DOWN), Blocks.FARMLAND.getDefaultState());
                    }
                    seeds.onItemUse(player, world, pos.offset(EnumFacing.DOWN), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
                    return 1;
                }
            }
        } else {
            pointer = 0;
        }
        return 1;
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

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new LockedInventoryTogglePiece(18 * 6 + 2, 27, this, EnumDyeColor.GREEN));
        return pieces;
    }

    private int getFilteredSlot(BlockPos pos) {
        int radius = getRadius();
        int x = Math.round(1.49F * (pos.getX() - this.pos.getX()) / radius);
        int z = Math.round(1.49F * (pos.getZ() - this.pos.getZ()) / radius);
        return 4 + x + 3 * z;
    }
}
