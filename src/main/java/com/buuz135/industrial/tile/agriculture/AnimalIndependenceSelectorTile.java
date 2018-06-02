package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.api.IAcceptsAdultFilter;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AnimalIndependenceSelectorTile extends WorkingAreaElectricMachine implements IAcceptsAdultFilter {

    public AnimalIndependenceSelectorTile() {
        super(AnimalIndependenceSelectorTile.class.getName().hashCode());
    }


    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityAgeable> animals = this.world.getEntitiesWithinAABB(EntityAgeable.class, area);
        if (animals.size() == 0) return 0;
        EntityAgeable animal = animals.get(0);
        boolean hasAddon = this.hasAddon(AdultFilterAddonItem.class);
        while (animal.isChild() == hasAddon && animals.indexOf(animal) + 1 < animals.size())
            animal = animals.get(animals.indexOf(animal) + 1);
        if (animal.isChild() == hasAddon) return 0;
        BlockPos pos = this.getPos().offset(this.getFacing(), 1);
        animal.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return 1;
    }

    @Override
    public boolean hasAddon() {
        return this.hasAddon(AdultFilterAddonItem.class);
    }
}
