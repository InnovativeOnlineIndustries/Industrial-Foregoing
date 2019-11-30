package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class AnimalFeederTile extends IndustrialAreaWorkingTile {

    @Save
    private SidedInvHandler input;

    public AnimalFeederTile() {
        super(ModuleAgricultureHusbandry.ANIMAL_FEEDER, RangeManager.RangeType.BEHIND);
        addInventory(input = (SidedInvHandler) new SidedInvHandler("food", 53, 22, 6 * 3, 0)
                .setColor(DyeColor.BLUE)
                .setOutputFilter((stack, integer) -> false)
                .setRange(6, 3)
                .setTile(this)
        );
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(400)) {
            List<AnimalEntity> mobs = this.world.getEntitiesWithinAABB(AnimalEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() == 0 || mobs.size() > 35) return new WorkAction(1, 0);
            mobs.removeIf(animalEntity -> animalEntity.isChild() || animalEntity.getGrowingAge() != 0 || !animalEntity.canBreed() || animalEntity.isInLove() || getFeedingItem(animalEntity).isEmpty());
            for (AnimalEntity firstParent : mobs) {
                for (AnimalEntity secondParent : mobs) {
                    if (firstParent.equals(secondParent) || !firstParent.getClass().equals(secondParent.getClass()))
                        continue;
                    ItemStack stack = getFeedingItem(firstParent);
                    ItemStack original = stack.copy();
                    stack.shrink(1);
                    stack = getFeedingItem(secondParent);
                    if (stack.isEmpty()) {
                        original.setCount(1);
                        ItemHandlerHelper.insertItem(input, original, false);
                        continue;
                    }
                    stack.shrink(1);
                    firstParent.setInLove(null);
                    secondParent.setInLove(null);
                    return new WorkAction(0.5f, 400);
                }
            }
        }
        return new WorkAction(1, 0);
    }

    private ItemStack getFeedingItem(AnimalEntity entity) {
        for (int i = 0; i < input.getSlots(); i++) {
            if (entity.isBreedingItem(input.getStackInSlot(i))) return input.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }


}
