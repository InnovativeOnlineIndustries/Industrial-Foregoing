package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

public class AnimalGrowthIncreaserTile extends WorkingAreaElectricMachine {

    private ItemStackHandler items;

    public AnimalGrowthIncreaserTile() {
        super(AnimalGrowthIncreaserTile.class.getName().hashCode(), 2, 2, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        items = new ItemStackHandler(6 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                AnimalGrowthIncreaserTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(items, EnumDyeColor.GREEN, "Food items", 18 * 3, 25, 6, 3));
        this.addInventoryToStorage(items, "items");
    }

    @Override
    public float work() {
        world.getEntitiesWithinAABB(EntityAnimal.class, getWorkingArea()).stream().filter(EntityAgeable::isChild).forEach(entityAnimal -> {
            for (int i = 0; i < items.getSlots(); ++i) {
                if (entityAnimal.isBreedingItem(items.getStackInSlot(i))) {
                    entityAnimal.ageUp(30, true);
                    items.getStackInSlot(i).shrink(1);
                    break;
                }
            }
        });
        return 1;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1);
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(corner1).grow(getRadius(), 0, getRadius()).setMaxY(this.getPos().getY() + getHeight());
    }

}
