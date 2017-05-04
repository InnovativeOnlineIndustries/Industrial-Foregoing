package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

public class BlockDestroyerTile extends WorkingAreaElectricMachine {


    private ItemStackHandler outItems;

    public BlockDestroyerTile() {
        super(BlockDestroyerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outItems = new ItemStackHandler(3*6);
        this.addInventory(new ColoredItemHandler(outItems, EnumDyeColor.ORANGE,"Broken items", new BoundingRectangle(18 * 3, 25, 18 * 3, 18 * 6)){
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return super.canInsertItem(slot, stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
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
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.ORANGE));

                return pieces;
            }
        });
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(new BlockPos(0,0,0).offset(this.getFacing().getOpposite()));
    }

    @Override
    protected float performWork() {
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea());
        for (BlockPos pos : blockPosList){
            if (!this.world.isAirBlock(pos)){
                Block block = this.world.getBlockState(pos).getBlock();
                List<ItemStack> drops = block.getDrops(this.world, pos, this.world.getBlockState(pos),0);
                boolean canInsert = true;
                for (ItemStack stack : drops){
                    if (!ItemHandlerHelper.insertItem(outItems,stack,true).isEmpty()){
                        canInsert = false;
                        break;
                    }
                }
                if (canInsert){
                    for (ItemStack stack : drops){
                        ItemHandlerHelper.insertItem(outItems,stack,false);
                    }
                    this.world.setBlockToAir(pos);
                    return 1;
                }
            }
        }
        return 0;
    }
}
