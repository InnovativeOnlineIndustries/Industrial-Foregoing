package com.buuz135.industrial.tile.animal;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class AnimalResourceHarvesterTile extends WorkingAreaElectricMachine {

    private ItemStackHandler outItems;

    public AnimalResourceHarvesterTile() {
        super(AnimalResourceHarvesterTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outItems = new ItemStackHandler(3 * 6);
//        this.addInventory(new ColoredItemHandler(outItems, EnumDyeColor.GREEN, "Fish output", new BoundingRectangle(18 * 3, 25, 18 * 6, 18 * 3)) {
//            @Override
//            public boolean canInsertItem(int slot, ItemStack stack) {
//                return false;
//            }
//
//            @Override
//            public boolean canExtractItem(int slot) {
//                return true;
//            }
//
//            @Override
//            public List<Slot> getSlots(BasicTeslaContainer container) {
//                List<Slot> slots = super.getSlots(container);
//                BoundingRectangle box = this.getBoundingBox();
//                int i = 0;
//                for (int y = 0; y < 3; y++) {
//                    for (int x = 0; x < 6; x++) {
//                        slots.add(new FilteredSlot(this.getItemHandlerForContainer(), i, box.getLeft() + 1 + x * 18, box.getTop() + 1 + y * 18));
//                        ++i;
//                    }
//                }
//                return slots;
//            }
//
//            @Override
//            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
//                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
//
//                BoundingRectangle box = this.getBoundingBox();
//                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
//                        6, 3,
//                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));
//
//                return pieces;
//            }
//        });
//        this.addInventoryToStorage(outFish, "water_resource_collector_out");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        int r = 2;
        int h = 2;
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, r + 1);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(r, 0, r).setMaxY(this.getPos().getY() + h);
    }

    @Override
    protected float performWork() {
        List<EntitySheep> animals = this.world.getEntitiesWithinAABB(EntitySheep.class, getWorkingArea());
        for (EntitySheep sheep : animals) {
            if (!sheep.getSheared()) {
                List<ItemStack> stacks = sheep.onSheared(new ItemStack(Items.SHEARS), this.world, null, 0);
            }
        }
        return 1;
    }
}
