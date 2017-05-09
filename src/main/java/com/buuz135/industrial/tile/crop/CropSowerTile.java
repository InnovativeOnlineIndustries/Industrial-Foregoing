package com.buuz135.industrial.tile.crop;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.client.infopiece.CropSowerFilterInfoPiece;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import net.minecraft.block.BlockFarmland;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
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

import java.util.Arrays;
import java.util.List;

public class CropSowerTile extends WorkingAreaElectricMachine {

    private static final String NBT_POINTER = "pointer";
    private static final String NBT_FILTER = "filter";
    private static final String NBT_NAME = "name";
    private static final String NBT_DATA = "data";

    private ItemStackHandler inPlant;
    private ItemStackHandler filter;
    private ItemStack[] filterStorage;
    private IFluidTank waterTank;
    private int pointer;

    public CropSowerTile() {
        super(CropSowerTile.class.getName().hashCode(), 1, 1);
        filterStorage = new ItemStack[9];
        Arrays.fill(filterStorage, ItemStack.EMPTY);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        waterTank = this.addFluidTank(FluidRegistry.WATER, 8000, EnumDyeColor.BLUE, "Water tank", new BoundingRectangle(50, 25, 18, 54));
        inPlant = new ItemStackHandler(12);
        this.addInventory(new ColoredItemHandler(inPlant, EnumDyeColor.GREEN, "Seeds input", new BoundingRectangle(18 * 5 + 3, 25, 18 * 4, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemSeeds || stack.getItem() instanceof ItemSeedFood || ItemStackUtils.isStackOreDict(stack, "treeSapling");
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
        filter = new ItemStackHandler(9);
        this.addInventory(new ColoredItemHandler(filter, EnumDyeColor.WHITE, "Filter", new BoundingRectangle(-18 * 4 + 14, 25, 18 * 3, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if (stack.getItem() instanceof ItemSeeds || stack.getItem() instanceof ItemSeedFood || ItemStackUtils.isStackOreDict(stack, "treeSapling")) {
                    ItemStack clone = stack.copy();
                    clone.setCount(1);
                    filterStorage[slot] = clone;
                } else {
                    filterStorage[slot] = ItemStack.EMPTY;
                }
                return false;
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
                    for (int x = 0; x < 3; x++) {
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
                int i = 0;
                for (EnumDyeColor color : new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.YELLOW, EnumDyeColor.LIME, EnumDyeColor.CYAN, EnumDyeColor.WHITE, EnumDyeColor.BLUE, EnumDyeColor.PURPLE, EnumDyeColor.MAGENTA, EnumDyeColor.BLACK}) {
                    pieces.add(new TiledRenderedGuiPiece(box.getLeft() + 18 * (i % 3), box.getTop() + (18 * (i / 3)), 18, 18, 1, 1, BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, color));
                    ++i;
                }

                return pieces;
            }
        });

    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        BlockPos corner1 = new BlockPos(0, 2, 0);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(getRadius(), 0, getRadius());
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        ++pointer;
        if (pointer >= blockPos.size()) pointer = 0;
        for (BlockPos pos : blockPos) {
            if (this.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().equals(Blocks.FARMLAND) && this.world.getBlockState(pos.offset(EnumFacing.DOWN)).getValue(BlockFarmland.MOISTURE) <= 6 && waterTank.getFluidAmount() > 50) {
                this.world.setBlockState(pos.offset(EnumFacing.DOWN), this.world.getBlockState(pos.offset(EnumFacing.DOWN)).withProperty(BlockFarmland.MOISTURE, 7));
                waterTank.drain(50, true);
            }
        }
        if (pointer < blockPos.size()) {
            BlockPos pos = blockPos.get(pointer);
            if (this.world.isAirBlock(pos)) {
                FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                ItemStack stack = getFirstItem(pos);
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

    private ItemStack getFirstItem(BlockPos pos) {
        int slot = getFilteredSlot(pos);
        for (int i = 0; i < inPlant.getSlots(); ++i)
            if (!inPlant.getStackInSlot(i).isEmpty() && filterStorage[slot] != null && !filterStorage[slot].isEmpty() &&
                    filterStorage[slot].getItem().equals(inPlant.getStackInSlot(i).getItem()) && filterStorage[slot].getMetadata() == inPlant.getStackInSlot(i).getMetadata())
                return inPlant.getStackInSlot(i);
        return ItemStack.EMPTY;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_POINTER, pointer);
        NBTTagCompound filterComp = new NBTTagCompound();
        int i = 0;
        for (ItemStack filter : filterStorage) {
            if (filter != null && !filter.isEmpty()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(NBT_NAME, filter.getItem().getRegistryName().toString());
                tag.setInteger(NBT_DATA, filter.getMetadata());
                filterComp.setTag(String.valueOf(i), tag);
            }
            ++i;
        }
        tagCompound.setTag(NBT_FILTER, filterComp);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_POINTER)) pointer = 0;
        else pointer = compound.getInteger(NBT_POINTER);
        filterStorage = new ItemStack[9];
        if (compound.hasKey(NBT_FILTER)) {
            NBTTagCompound filterComp = compound.getCompoundTag(NBT_FILTER);
            for (int i = 0; i < 9; ++i) {
                filterStorage[i] = ItemStack.EMPTY;
                if (filterComp.hasKey(String.valueOf(i))) {
                    NBTTagCompound tag = filterComp.getCompoundTag(String.valueOf(i));
                    Item item = Item.getByNameOrId(tag.getString(NBT_NAME));
                    if (item != null) {
                        filterStorage[i] = new ItemStack(item, 1, tag.getInteger(NBT_DATA));
                    }
                }
            }
        }
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new CropSowerFilterInfoPiece(this, -18 * 4 + 7, 19));
        return pieces;
    }

    public ItemStack[] getFilterStorage() {
        return filterStorage;
    }

    private int getFilteredSlot(BlockPos pos) {
        int radius = getRadius();
        int x = Math.round(1.49F * (pos.getX() - this.pos.getX()) / radius);
        int z = Math.round(1.49F * (pos.getZ() - this.pos.getZ()) / radius);
        return 4 + x + 3 * z;
    }
}
