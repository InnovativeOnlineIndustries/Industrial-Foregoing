package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class AnimalStockIncreaserTile extends WorkingAreaElectricMachine {

    public ItemStackHandler inFeedItems;

    public AnimalStockIncreaserTile() {
        super(AnimalStockIncreaserTile.class.getName().hashCode(), 2, 2, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inFeedItems = new ItemStackHandler(3 * 6) {
            @Override
            protected void onContentsChanged(int slot) {
                AnimalStockIncreaserTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(inFeedItems, EnumDyeColor.GREEN, "Food items", 18 * 3, 25, 6, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

        });
        this.addInventoryToStorage(inFeedItems, "animal_stock_in");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(getRadius(), 0, getRadius()).setMaxY(this.getPos().getY() + getHeight());
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock) this.getBlockType()).isWorkDisabled()) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityAnimal> animals = this.world.getEntitiesWithinAABB(EntityAnimal.class, area);
        if (animals.size() == 0 || animals.size() > 20) return 0;
        EntityAnimal animal1 = animals.get(0);
        while ((animal1.isChild() || animal1.getGrowingAge() != 0 || getFirstBreedingItem(animal1).isEmpty()) && animals.indexOf(animal1) + 1 < animals.size())
            animal1 = animals.get(animals.indexOf(animal1) + 1);
        if (animal1.isChild() || animal1.getGrowingAge() != 0) return 0;
        EntityAnimal animal2 = animals.get(0);
        while ((animal2.equals(animal1) || animal2.isChild() || animal2.getGrowingAge() != 0 || getFirstBreedingItem(animal2).isEmpty()) && animals.indexOf(animal2) + 1 < animals.size())
            animal2 = animals.get(animals.indexOf(animal2) + 1);
        if (animal2.equals(animal1) || animal2.isChild() || animal2.getGrowingAge() != 0) return 0;
        if (animal1.getClass() != animal2.getClass()) return 0;
        ItemStack stack = getFirstBreedingItem(animal1);
        Item item = stack.getItem();
        stack.setCount(stack.getCount() - 1);
        stack = getFirstBreedingItem(animal2);
        if (stack.isEmpty()) {
            ItemHandlerHelper.insertItem(inFeedItems, new ItemStack(item, 1), false);
            return 0;
        }
        stack.setCount(stack.getCount() - 1);
        animal1.setInLove(null);
        animal2.setInLove(null);

        return 1;
    }

    public ItemStack getFirstBreedingItem(EntityAnimal animal) {
        for (int i = 0; i < inFeedItems.getSlots(); ++i) {
            if (animal.isBreedingItem(inFeedItems.getStackInSlot(i))) return inFeedItems.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }



}
