package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.items.ItemStackHandler;

public class AnimalGrowthIncreaserTile extends WorkingAreaElectricMachine {

    private ItemStackHandler items;

    public AnimalGrowthIncreaserTile() {
        super(AnimalGrowthIncreaserTile.class.getName().hashCode());
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

}
