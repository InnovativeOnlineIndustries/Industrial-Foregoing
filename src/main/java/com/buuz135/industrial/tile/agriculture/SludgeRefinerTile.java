package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.tile.block.SludgeRefinerBlock;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class SludgeRefinerTile extends CustomElectricMachine {

    private IFluidTank tank;
    private ItemStackHandler outItems;

    public SludgeRefinerTile() {
        super(SludgeRefinerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.SLUDGE,8000, EnumDyeColor.BLACK,"Sludge tank",new BoundingRectangle(50, 25, 18, 54));
        outItems = new ItemStackHandler(4*3){
            @Override
            protected void onContentsChanged(int slot) {
                SludgeRefinerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems,EnumDyeColor.BROWN,"Output items", 18*5+3,25,4,3){
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outItems,"outItems");
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;

        if (tank.getFluid() != null && tank.getFluidAmount() >= 1000){
            SludgeRefinerBlock block = (SludgeRefinerBlock) this.getBlockType();
            ItemStackWeightedItem itemStack = WeightedRandom.getRandomItem(this.world.rand,block.getItemStackWeightedItems());
            if (ItemHandlerHelper.insertItem(outItems,itemStack.getStack(),true).isEmpty()){
                tank.drain(1000,true);
                ItemHandlerHelper.insertItem(outItems,itemStack.getStack().copy(),false);
                return 1;
            }
        }

        return 0;
    }
}
